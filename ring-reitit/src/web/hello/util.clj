(ns web.hello.util)

(defn base64-encode [bs]
  (.encodeToString (java.util.Base64/getEncoder) bs))

(defn base64-decode [s]
  (.decode (java.util.Base64/getDecoder) s))
