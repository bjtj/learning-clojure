(def length (or (some-> (first *command-line-args*) parse-long) 32))

(def ^:private chars
  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")

(def ^:private base
  (java.math.BigInteger/valueOf 62))

(defn encode
  [^bytes bytes]
  (loop [value (java.math.BigInteger. 1 bytes)
         result ()]
    (if (zero? (.signum value))
      (if (empty? result)
        "0"
        (apply str result))
      (let [[q r] (.divideAndRemainder value base)]
        (recur q
               (conj result (.charAt chars (.intValue r))))))))

(defn generate-secret [length]
  (let [buffer (byte-array length)]
    (.nextBytes (java.security.SecureRandom/getInstanceStrong) buffer)
    (encode buffer)))

(println (generate-secret length))
