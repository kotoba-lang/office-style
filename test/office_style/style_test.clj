(ns office-style.style-test
  (:require [clojure.test :refer [deftest is]]
            [office-style.style :as style])
  (:import [java.io ByteArrayOutputStream]
           [java.util.zip ZipEntry ZipOutputStream]))

(defn zip-bytes [entries]
  (let [out (ByteArrayOutputStream.)]
    (with-open [zip (ZipOutputStream. out)]
      (doseq [[path text] entries]
        (.putNextEntry zip (ZipEntry. path))
        (.write zip (.getBytes text "UTF-8"))
        (.closeEntry zip)))
    (.toByteArray out)))

(deftest extracts-deterministic-style-ir
  (let [bytes (zip-bytes
               {"ppt/presentation.xml" "<p:presentation><p:sldSz cx=\"9144000\" cy=\"5143500\" type=\"wide\"/></p:presentation>"
                "ppt/theme/theme1.xml" "<a:theme><a:clrScheme><a:accent1><a:srgbClr val=\"496b9a\"/></a:accent1></a:clrScheme><a:fontScheme><a:majorFont><a:latin typeface=\"Aptos Display\"/></a:majorFont><a:minorFont><a:latin typeface=\"Aptos\"/></a:minorFont></a:fontScheme></a:theme>"
                "ppt/slides/slide1.xml" "<p:sld/>"
                "ppt/slideLayouts/slideLayout1.xml" "<p:sldLayout/>"
                "ppt/slideMasters/slideMaster1.xml" "<p:sldMaster/>"})
        ir (style/extract-bytes bytes)]
    (is (= "496B9A" (get-in ir [:office-style/colors :office-style.color/accent1])))
    (is (= "Aptos Display" (get-in ir [:office-style/fonts :office-style.font/majorFont])))
    (is (= {:office-style/cx 9144000
            :office-style/cy 5143500
            :office-style/type :wide}
           (:office-style/slide-size ir)))
    (is (= ["ppt/slides/slide1.xml"] (:office-style/slides ir)))))
