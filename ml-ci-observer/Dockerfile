FROM node:8

WORKDIR /app/observer

COPY package*.json ./

RUN npm install
RUN npm install -g typescript

COPY src/ src/
RUN npm run build

CMD [ "node", "build/index.js" ]