# clj-grpc

Clojure gRPC

## Guide

- Create Clojure app
```sh
lein new app clj-grpc
```

- Update `project.clj` to separate java & clojure source:
```clj
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
```

- Add sample proto file [src/proto/helloworld.proto](src/proto/helloworld.proto)

- Generating the code using Protocol Buffer Compiler
  - Download latest protoc from https://github.com/protocolbuffers/protobuf/releases
  - Obtain the [gRPC Java Codegen Plugin](https://github.com/grpc/grpc-java/tree/master/compiler)
  - `protoc --plugin=protoc-gen-grpc-java=$PATH_TO_PLUGIN -I=$SRC_DIR
    --java_out=$DST_DIR --grpc-java_out=$DST_DIR $SRC_DIR/helloworld.proto`

- Using lein plugin
  - https://github.com/bsima/lein-protoc
    ```clj
    :plugins [[lein-protoc "0.4.0"]]
    ; lein-protoc config
    :proto-version ~proto-version
    :protoc-grpc {:version ~grpc-version}
    :proto-source-paths ["src/proto"] ; default
    :proto-target-path "src/java"
    ```
  - `lein protoc`

- Add grpc deps
```clj
;; ref https://github.com/grpc/grpc-java
[io.grpc/grpc-netty-shaded ~grpc-version]
[io.grpc/grpc-protobuf ~grpc-version]
[io.grpc/grpc-stub ~grpc-version]
[org.apache.tomcat/annotations-api "6.0.53"] ; necessary for Java 9+
```

- Creating the server
  - impl service
  ```clj
  (defn greeter-service []
    (proxy [GreeterGrpc$GreeterImplBase] []
      (sayHello [^HelloRequest request response]
        (let [name (.getName request)
              builder (-> (HelloReply/newBuilder)
                          (.setMessage (str "Hello " name)))]
          (.onNext response (.build builder))
          (.onCompleted response)))))
  ```
  - start server
  ```clj
  (let [server (-> (ServerBuilder/forPort 9090)
                   (.addService (greeter-service))
                   (.build))]
    (.start server)
    (println "Server started, listening on 9090")
    (.awaitTermination server))
  ```
  
- Creating the client
```clj
(let [channel (-> (ManagedChannelBuilder/forAddress "localhost" 9090)
                    (.usePlaintext)
                    (.build))
        stub (GreeterGrpc/newBlockingStub channel)
        req (-> (HelloRequest/newBuilder)
                (.setName "test")
                (.build))
        resp (.sayHello stub req)]
    (prn resp)
    (.shutdown channel))
```
// TODO https://github.com/AppsFlyer/pronto/