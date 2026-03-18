(ns commonmark-example.core
  (:import (org.commonmark.node Node)
           (org.commonmark.parser Parser)
           (org.commonmark.renderer.html HtmlRenderer)
           (org.commonmark.ext.gfm.tables TablesExtension)))

(defn render [md-str]
  (let [exts     [(TablesExtension/create)]
        parser   (.. (Parser/builder) (extensions exts) build)
        renderer (.. (HtmlRenderer/builder) (extensions exts) build)
        doc      (. parser parse md-str)]
    (. renderer render doc)))

(comment
  (render "This is *Sparta*")
  (render "# Hello
| foo | bar |
|-----|-----|
| baz | bim |
")
  ;; 
  )
