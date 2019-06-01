FROM node:8

WORKDIR /app/observer

COPY package*.json ./

RUN npm install

COPY build/* ./

CMD [ "node", "index.js" ]