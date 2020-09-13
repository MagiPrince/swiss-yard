package ch.hepia.swissyard.communication;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class Broker {
    /**
     * Constructor Broker
     * 
     * @param ipBroker the ip of the Broker used to communicate
     */
    public Broker(String ipBroker) {
        try {
            final String brokerUrl = "tcp://" + ipBroker + ":61616";
            final BrokerService broker = new BrokerService();

            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(brokerUrl);
            broker.start();

            final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            final Connection connection = connectionFactory.createConnection();
            connection.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}