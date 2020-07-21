# graph-based-data-lake project

An ETL application which is written in Quarkus, Spark SQL Streaming, Neo4j and various types of Databases and stores. It also covers the devops frameworks like docker and Kubernetes.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.

## Explanation

You can follow this series to know stage by stage implementation: 
[Full Stack: Remastering Master Data Management into graph like data](https://hashnode.com/series/full-stack-remastering-master-data-management-into-graph-like-data-ckbyotel2009raws15hejhgzp)

You can find the repo being used for:
* [Introduction and Prerequisites: Need for data-hub or data-lake for MDM](https://arpendu.hashnode.dev/1-introduction-and-prerequisites-need-for-data-hub-or-data-lake-for-mdm-ckbyrlq8h00b2bus1e8ar1z23)
* [Quarkus: yet another framework like Spring Boot](https://arpendu.hashnode.dev/2-quarkus-yet-another-framework-like-spring-boot-ckbza8m2a000lgps17p476n3r)
* [Reactive Stream messaging in Java using Quarkus](https://arpendu.hashnode.dev/3-reactive-stream-messaging-in-java-using-quarkus-ckc269cqu00g091s1el5pgjdn)
* [Spark SQL Structured Streaming with Kafka](https://arpendu.hashnode.dev/4-spark-sql-structured-streaming-with-kafka-ckc7aez4900777is1ekl66p9m)
* [Graph like data using Neo4j Graph Database](https://arpendu.hashnode.dev/5-graph-like-data-using-neo4j-graph-database-ckc7ulc9u001xups11tud2rqj)
* [Build your application and containerize using Docker and Kubernetes](https://arpendu.hashnode.dev/7-build-your-application-and-containerize-using-docker-and-kubernetes-ckchh87lw001bbas19a617ov2)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw clean compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw clean package`.
It produces the `graph-data-lake-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/graph-data-lake-1.0.0-SNAPSHOT-runner.jar`.
It also creates automated Kuberenetes yaml and json in /target/kubernetes/kubernetes.yml or /target/kubernetes/kubernetes.json which defines the Deployment and Service component required to run the container.

## Creating a native executable

You can create a native executable using: `./mvnw clean package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw clean package -Pnative -Dquarkus.native.container-build=true`.
You can then execute your native executable with: `./target/graph-data-lake-1.0.0-SNAPSHOT-runner`

Or, if you want to run a minimal install docker build, the you can run the executable build using: `./mvnw clean package -Pnative -Dnative-image.docker-build=true`.

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## Build Docker image

You can build a docker image using: `docker build -f src/main/docker/Dockerfile.jvm -t quarkus/graph-data-lake-jvm`
Then run the container using: `docker run -i --rm -p 8080:8080 quarkus/graph-data-lake-jvm`

If you want to include the debug port into your docker image you will have to expose the debug port (default 5005) like this : ` EXPOSE 8080 5050 `
Then run the container using : `docker run -i --rm -p 8080:8080 -p 5005:5005 -e JAVA_ENABLE_DEBUG="true" quarkus/graph-data-lake-jvm`

## Build and Run Kubernetes cluster

If you have minikube installed, then check for `minikube status`
Update the image name and tag after docker build in kubernetes.yml and kuberenets.json

Now run your cluster using: `kubectl apply -f target/kubernetes/kubernetes.yml`
If you want to scale the application to multiple replicas, the you can use: `kubectl scale --replicas=10 --deployment graph-data-lake`

It takes only few seconds to spin up those containers.
