(def proto-version "3.19.4")
(def grpc-version "1.45.0")

(defproject clj-grpc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.11.0"]
                 ;; ref https://github.com/grpc/grpc-java
                 [io.grpc/grpc-netty-shaded ~grpc-version]
                 [io.grpc/grpc-protobuf ~grpc-version]
                 [io.grpc/grpc-stub ~grpc-version]
                 [org.apache.tomcat/annotations-api "6.0.53"] ; necessary for Java 9+
                 ]
  :main ^:skip-aot clj-grpc.core
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-protoc "0.4.0"]]
  ; lein-protoc config
  :proto-version ~proto-version
  :protoc-grpc {:version ~grpc-version}
  :proto-source-paths ["src/proto"]
  :proto-target-path "src/java")
