docker-compose down
docker build -t backend:latest .
docker build -t frontend ./frontend/CNABProcessor
docker-compose up --build --force-recreate --remove-orphans