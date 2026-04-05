import { useEffect, useState } from 'react'
import axios from 'axios';

function App() {
  const [transactions, setTransactions] = useState([]);
  const [file, setFile] = useState(null);

  const dateFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };

  const fetchTransactions = async () => {
    const response = await axios.get("http://localhost:8080/transactions");
    setTransactions(response.data);
    console.log(response.data);
  }

  const handleFileChange = async (e) => {
    setFile(e.target.files[0]);
  }

  const uploadFile = async () => {
    const formData = new FormData();
    formData.append('file', file);
    axios.post("http://localhost:8080/cnab/upload", formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }

  useEffect(() => {
    fetchTransactions();
  }, [])

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-6xl mx-auto space-y-6">
        <div className="bg-white shadow rounded-2xl p-6">
          <h1 className="text-2xl font-bold text-gray-800">CNAB import</h1>
        </div>
        <div className="bg-white shadow rounded-2xl p-6 flex flex-col md:flex-row md:items-center gap-4">
          <span className="text-gray-600 font-medium">Choose file</span>

          <input
            type="file"
            onChange={handleFileChange}
            name="uploadCNAB"
            id="uploadCNAB"
            className="block w-full text-sm text-gray-500
              file:mr-4 file:py-2 file:px-4
              file:rounded-lg file:border-0
              file:text-sm file:font-semibold
              file:bg-blue-50 file:text-blue-700
              hover:file:bg-blue-100"
          />

          <button
            type="button"
            onClick={uploadFile}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition"
          >
            Upload
          </button>
        </div>
        <div className="bg-white shadow rounded-2xl p-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">
            Transactions
          </h2>

          <div className="space-y-6">
            {transactions.map((report, key) => {
              return (
                <div key={key} className="overflow-x-auto">
                  <table className="min-w-full border border-gray-200 rounded-lg overflow-hidden">
                    
                    <thead className="bg-gray-100 text-gray-700 text-sm uppercase">
                      <tr>
                        <th className="px-4 py-2 text-left">{report.storeName}</th>
                        <th className={`px-4 py-2 font-medium ${
                                report.total > 0
                                  ? 'text-green-600'
                                  : 'text-red-600'
                            }`}>
                              {report.total}
                        </th>
                      </tr>
                      <tr>
                        <th className="px-4 py-2 text-left">Card</th>
                        <th className="px-4 py-2 text-left">CPF</th>
                        <th className="px-4 py-2 text-left">Date</th>
                        <th className="px-4 py-2 text-left">Store owner</th>
                        <th className="px-4 py-2 text-left">Hour</th>
                        <th className="px-4 py-2 text-left">Store name</th>
                        <th className="px-4 py-2 text-left">Type</th>
                        <th className="px-4 py-2 text-left">Value</th>
                      </tr>
                    </thead>

                    <tbody className="text-gray-700 text-sm">
                      {report.transactions.map((transaction, key) => {
                        return (
                          <tr
                            key={key}
                            className="border-t hover:bg-gray-50 transition"
                          >
                            <td className="px-4 py-2">{transaction.card}</td>
                            <td className="px-4 py-2">{transaction.cpf}</td>
                            <td className="px-4 py-2">{new Intl.DateTimeFormat('en-US', dateFormatOptions).format(new Date(transaction.date))}</td>
                            <td className="px-4 py-2">{transaction.storeOwner}</td>
                            <td className="px-4 py-2">{transaction.hour}</td>
                            <td className="px-4 py-2">{transaction.storeName}</td>
                            <td className="px-4 py-2">{transaction.type}</td>
                            <td className={`px-4 py-2 font-medium ${
                                transaction.value > 0
                                  ? 'text-green-600'
                                  : 'text-red-600'
                            }`}>
                              {transaction.value}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>

                  </table>
                </div>
              );
            })}
          </div>
        </div>

      </div>
    </div>
  )
}

export default App
