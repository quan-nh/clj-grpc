(ns clj-grpc.core
  (:gen-class)
  (:require [pronto.core :as p])
  (:import [examples.helloworld GreeterGrpc$GreeterImplBase HelloReply GreeterGrpc HelloRequest]
           (io.grpc ServerBuilder ManagedChannelBuilder)))

(p/defmapper hello-reply-mapper [HelloReply])

(defn greeter-service []
  (proxy [GreeterGrpc$GreeterImplBase] []
    (sayHello [request response]
      (let [name (.getName request)
            hello-reply (-> (p/proto-map hello-reply-mapper HelloReply)
                            (assoc :message (str "Hello " name)))]
        (.onNext response (p/proto-map->proto hello-reply))
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