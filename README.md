# Distributed Complex Event Processing with RabbitMQ

## Description

This project aims to implement a distributed Complex Event Processing (CEP) system using RabbitMQ and Docker. The goal is to deploy CEP queries across multiple nodes, each evaluating specific sub-queries and forwarding the results to other nodes for further processing. The queries are defined based on a custom format and deployed following a publish-subscribe model.

## Features

- Distributed processing of CEP queries.
- Nodes communicate via RabbitMQ, ensuring reliable inter-node communication.
- Each node runs in a Docker container, providing isolation and scalability.
- Custom-designed queries that define complex event patterns.
- Distributed evaluation plans to assign queries and corresponding input events to specific nodes.

## Setup

To set up the project, follow the steps below:

1. Install Docker and RabbitMQ on your machine. Docker is used to create nodes, and RabbitMQ is used for inter-node communication.
2. Clone the project repository.
3. Navigate to the project directory.
4. Run the `rabbitMQ_Cluster_Startup.sh` script to start the RabbitMQ cluster.
5. Build the project using Maven by running `mvn clean install`.
6. Start the project.

## Usage

After setting up the project, you can start processing events. The events and the queries should be defined according to the provided format. The processing nodes will start working based on the distributed evaluation plans, and the results will be available in each node's console.

## Dependencies

The project has the following dependencies:

- Docker
- RabbitMQ
- Esper (a CEP engine)
- Maven (for building the project)

## System Requirements

The system requirements for running the project include:

- A machine with Docker and RabbitMQ installed.
- Enough memory and CPU resources to run the desired number of Docker nodes.

## Contributing

If you wish to contribute to this project, please fork the repository and submit a pull request. 

## License

This project is licensed under the terms of the MIT license.
