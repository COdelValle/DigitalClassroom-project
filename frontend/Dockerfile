# Development stage - Vite dev server
FROM node:20-alpine
WORKDIR /app

# Instalar dependencias
COPY package*.json ./
RUN npm install

# Copiar código fuente
COPY . .

# Exponer puerto de Vite
EXPOSE 5173

# Comando para ejecutar dev server
CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
