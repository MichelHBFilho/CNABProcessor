package com.michelfilho.cnabprocessorapi.config;

import com.michelfilho.cnabprocessorapi.domain.CNABTransaction;
import com.michelfilho.cnabprocessorapi.domain.Transaction;
import com.michelfilho.cnabprocessorapi.domain.TransactionType;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistrySmartInitializingSingleton;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;

    public BatchConfig(
            PlatformTransactionManager transactionManager,
            JobRepository jobRepository
    ) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job cnabJob(@Qualifier("cnabStep") Step cnabStep) {
        return new JobBuilder("cnabJob", jobRepository)
                .start(cnabStep)
                .build();
    }

    @Bean
    Step cnabStep(
            ItemReader<CNABTransaction> reader,
            ItemProcessor<CNABTransaction, Transaction> processor,
            ItemWriter<Transaction> writer
    ) {
        return new StepBuilder("cnabStep" ,jobRepository)
                .<CNABTransaction, Transaction>chunk(1000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @StepScope
    @Bean
    FlatFileItemReader<CNABTransaction> reader(
            @Value("#{jobParameters['cnabFile']}") Resource resource
    ) {
        return new FlatFileItemReaderBuilder<CNABTransaction>()
                .name("reader")
                .resource(resource)
                .fixedLength()
                .strict(false)
                .columns(
                        new Range(1,1),
                        new Range(2, 9),
                        new Range(10,19),
                        new Range(20,30),
                        new Range(31,42),
                        new Range(43, 48),
                        new Range(49, 62),
                        new Range(63, 81)
                )
                .names("type",
                        "date",
                        "value",
                        "cpf",
                        "card",
                        "hour",
                        "storeOwner",
                        "storeName")
                .targetType(CNABTransaction.class)
                .build();
    }

    @Bean
    ItemProcessor<CNABTransaction, Transaction> processor() {
        return (CNABTransaction item) -> {
            TransactionType type = TransactionType.findByType(item.type());
            BigDecimal value = item.value()
                    .multiply(type.getSignal());
            return new Transaction(
                    null,
                    item.type(),
                    null,
                    item.value().divide(BigDecimal.valueOf(100)),
                    item.cpf(),
                    item.card(),
                    null,
                    item.storeOwner().stripTrailing(),
                    item.storeName().stripTrailing()
            )
                    .withValue(value)
                    .withDate(item.date())
                    .withHour(item.hour());
        };
    }

    @Bean
    JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO "transactions" (
                             "type", "date", "value", "cpf", "card", "hour", "store_owner", "store_name"
                         ) VALUES (
                             :type, :date, :value, :cpf, :card, :hour, :storeOwner, :storeName
                         )
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    public TaskExecutor taskExecuter() {
        return new SimpleAsyncTaskExecutor();
    }

}
