(ns clj-grpc.core
  (:gen-class)
  (:import [examples.helloworld GreeterGrpc$GreeterImplBase HelloReply GreeterGrpc HelloRequest]
           (io.grpc ServerBuilder ManagedChannelBuilder)))

(defn greeter-service []
  (proxy [GreeterGrpc$GreeterImplBase] []
    (sayHello [request response]
      (let [name (.getName request)
            builder (-> (HelloReply/newBuilder)
                        (.setMessage (str "Hello " name)))]
        (.onNext response (.build builder))
        (.onCompleted response)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [server (-> (ServerBuilder/forPort 9090)
                   (.addService (greeter-service))
                   (.build))]
    (.start server)
    (println "Server started, listening on 9090")
    (.awaitTermination server)))

(comment
  (let [channel (-> (ManagedChannelBuilder/forAddress "localhost" 9090)
                    (.usePlaintext)
                    (.build))
        stub (GreeterGrpc/newBlockingStub channel)
        req (-> (HelloRequest/newBuilder)
                (.setName "test")
                (.build))
        resp (.sayHello stub req)]
    (prn resp)
    (.shutdown channel)))