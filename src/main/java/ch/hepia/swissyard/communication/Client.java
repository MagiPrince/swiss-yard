package ch.hepia.swissyard.communication;

import java.util.ArrayList;
import java.util.Optional;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import ch.hepia.swissyard.*;
import ch.hepia.swissyard.view.View;

public class Client {
    private static int ackMode;
    private static String clientQueueName;

    private boolean transacted = false;

    private Optional<Session> maybeSession;
    private Optional<MessageProducer> maybeProducer;

    static {
        clientQueueName = "client.messages";
        ackMode = Session.AUTO_ACKNOWLEDGE;
    }

    /**
     * Constructor Client
     * 
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Client(final String ipBroker) {

        final String brokerUrl = "tcp://" + ipBroker + ":61616";

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(transacted, ackMode);
            maybeSession = Optional.of(session);

            Destination mainTopic = session.createTopic(clientQueueName);

            MessageProducer producer = session.createProducer(mainTopic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            maybeProducer = Optional.of(producer);

            MessageConsumer responseConsumer = session.createConsumer(mainTopic);

            responseConsumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        if(message instanceof TextMessage){
                            TextMessage textMessage = (TextMessage) message;
                            String messageText = textMessage.getText();
                            View.controller.handleMessage(messageText);
                        }
                        if(message instanceof MessageToCom){
                            MessageToCom messageToCom = (MessageToCom) message;
                            View.controller.handleMessage(messageToCom);
                        }
                    } catch (JMSException | UnhandledMessageException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message of type String
     * @param msg message to send
     */
    public void send(String msg) {
        maybeProducer.ifPresent(p -> maybeSession.ifPresent(s -> {
            try {
                TextMessage txt = s.createTextMessage(msg);
                p.send(txt);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * Sends a message of type MessageToCom
     * @param messageToCom message to send
     */
    public void send(MessageToCom messageToCom) {
        maybeProducer.ifPresent(p -> maybeSession.ifPresent(s -> {
            try {
                ObjectMessage obj = s.createObjectMessage(messageToCom);
                p.send(obj);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }));
    }

}