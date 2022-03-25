(ns clj-grpc.core
  (:gen-class)
  (:import [examples.helloworld GreeterGrpc$GreeterImplBase HelloReply]
           (io.grpc ServerBuilder)))

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
