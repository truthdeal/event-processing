#!/bin/bash

#stop and remove all running rabbitMQ containers
docker ps --filter "name=rabbit" --format "{{.Names}}" | xargs -I {} sh -c 'docker stop {} && docker rm {}'

# Create a Docker network for RabbitMQ containers
docker network create mynet


# Cluster the remaining RabbitMQ containers with the base node
docker run -d --hostname rabbit1 --name myrabbit1 -p 15672:15672 -p 5672:5672 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit2 --name myrabbit2 -p 5673:5672 --link myrabbit1:rabbit1 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit3 --name myrabbit3 -p 5674:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit4 --name myrabbit4 -p 5675:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit5 --name myrabbit5 -p 5678:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit6 --name myrabbit6 -p 5679:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --link myrabbit5:rabbit5 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit7 --name myrabbit7 -p 5680:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --link myrabbit5:rabbit5 --link myrabbit6:rabbit6 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit8 --name myrabbit8 -p 5681:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --link myrabbit5:rabbit5 --link myrabbit6:rabbit6 --link myrabbit7:rabbit7 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit9 --name myrabbit9 -p 5682:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --link myrabbit5:rabbit5 --link myrabbit6:rabbit6 --link myrabbit7:rabbit7 --link myrabbit8:rabbit8 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management
docker run -d --hostname rabbit10 --name myrabbit10 -p 5682:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 --link myrabbit3:rabbit3 --link myrabbit4:rabbit4 --link myrabbit5:rabbit5 --link myrabbit6:rabbit6 --link myrabbit7:rabbit7 --link myrabbit8:rabbit8 --link myrabbit9:rabbit9 --network mynet -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:management

#restart containers and cluster them

until [ "`docker inspect -f {{.State.Running}} myrabbit2`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit2 is running"

winpty docker exec -it myrabbit2 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"
echo "--------------------------------"

until [ "`docker inspect -f {{.State.Running}} myrabbit3`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit3 is running"

winpty docker exec -it myrabbit3 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"

until [ "`docker inspect -f {{.State.Running}} myrabbit4`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit4 is running"

winpty docker exec -it myrabbit4 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"
echo "--------------------------------"

until [ "`docker inspect -f {{.State.Running}} myrabbit5`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit5 is running"

winpty docker exec -it myrabbit5 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"

until [ "`docker inspect -f {{.State.Running}} myrabbit6`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit6 is running"
winpty docker exec -it myrabbit6 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"
echo "--------------------------------"

until [ "`docker inspect -f {{.State.Running}} myrabbit7`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit7 is running"

winpty docker exec -it myrabbit7 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"
echo "--------------------------------"
until [ "`docker inspect -f {{.State.Running}} myrabbit8`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit8 is running"

winpty docker exec -it myrabbit8 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"
echo "--------------------------------"
until [ "`docker inspect -f {{.State.Running}} myrabbit9`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit9 is running"

winpty docker exec -it myrabbit9 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"

echo "--------------------------------"
until [ "`docker inspect -f {{.State.Running}} myrabbit10`"=="true" ]; do
    sleep 0.1;
done;
echo "myrabbit10 is running"

winpty docker exec -it myrabbit10 bash -c "
  rabbitmqctl stop_app &&
  rabbitmqctl reset &&
  rabbitmqctl join_cluster --ram rabbit@rabbit1 &&
  rabbitmqctl start_app
"


echo "Startup finished"

read