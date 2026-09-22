(def length (or (some-> (first *command-line-args*) parse-long) 32))

(defn encode
  [^bytes bytes]
  (.encodeToString (java.util.Base64/getEncoder) bytes))

(defn generate-secret [length]
  (let [buffer (byte-array length)]
    (.nextBytes (java.security.SecureRandom/getInstanceStrong) buffer)
    (encode buffer)))

(println (generate-secret length))
