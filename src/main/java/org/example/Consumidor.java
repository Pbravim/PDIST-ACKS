package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
public class Consumidor {

    private static void doWork (String task) throws InterruptedException {
        for (char ch: task.toCharArray ()) {
            if (ch == '.') Thread.sleep (1000);
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Consumidor");

        String NOME_FILA = "filaTchau";

        //criando a fabrica de conexoes e criando uma conexao
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection conexao = connectionFactory.newConnection();

        //criando um canal e declarando uma fila
        Channel canal = conexao.createChannel();
        canal.queueDeclare(NOME_FILA, true, false, false, null);
        canal.basicQos(1);
        //Definindo a funcao callback
        DeliverCallback callback = (consumerTag, delivery) -> {
            String mensagem = new String(delivery.getBody());
            System.out.println("Recebi a mensagem: " + mensagem);

            try {
                doWork(mensagem);
            } catch (InterruptedException error) {
                System.out.println(error);
            } finally {
                System.out.println("[x] Feito");
                canal.basicAck(delivery.getEnvelope(). getDeliveryTag(), false);

            }
        };


        canal.basicConsume(NOME_FILA, false, callback, consumerTag -> {});
        System.out.println("Continuarei executando outras atividades enquanto não chega mensagem...");
    }
}
