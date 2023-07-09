#!/bin/bash

basePort = 5672
baseNode = "node"
rabbitmq-server -detached RABBITMQ_NODE_PORT=5673 RABBITMQ_NODENAME=node2
rabbitmq-server -detached RABBITMQ_NODE_PORT=5674 RABBITMQ_NODENAME=node3 
rabbitmq-server -detached RABBITMQ_NODE_PORT=5673 RABBITMQ_NODENAME=node4

for i in {1..$1}
do
	rabbitmq-server -detached RABBITMQ_NODE_PORT=$basePort++ RABBITMQ_NODENAME=node$i 
	$i = i
	rabbitmqctl -n node$i stop_app
	rabbitmqctl -n hare join_cluster rabbit@`hostname -s`
	rabbitmqctl -n hare start_app
done
