(ns office-style.style
  "Deterministic CLJC StyleIR extraction from PowerPoint OOXML."
  (:require [clojure.string :as str]
            [office-style.opc :as opc]))

(defn- attr [s k]
  (second (re-find (re-pattern (str "\\b" (name k) "=\"([^\"]+)\"")) s)))

(defn- srgb-colors [xml]
  (->> (re-seq #"<a:([A-Za-z0-9]+)>\s*<a:srgbClr val=\"([0-9A-Fa-f]{6})\"" xml)
       (map (fn [[_ role color]]
              [(keyword "office-style.color" role) (str/upper-case color)]))
       (into {})))

(defn- typefaces [xml]
  (->> (re-seq #"<a:(majorFont|minorFont)>.*?<a:latin typeface=\"([^\"]+)\"" xml)
       (map (fn [[_ role face]]
              [(keyword "office-style.font" role) face]))
       (into {})))

(defn- slide-size [presentation-xml]
  (when-let [tag (second (re-find #"<p:sldSz\b([^>]*)>" (or presentation-xml "")))]
    {:office-style/cx (parse-long (or (attr tag :cx) "0"))
     :office-style/cy (parse-long (or (attr tag :cy) "0"))
     :office-style/type (keyword (or (attr tag :type) "custom"))}))

(defn style-ir [pkg]
  (let [entries (:office-style/entries pkg)
        theme-xml (or (get entries "ppt/theme/theme1.xml") "")
        presentation-xml (get entries "ppt/presentation.xml")
        slides (->> entries
                    keys
                    (filter #(re-matches #"ppt/slides/slide\d+\.xml" %))
                    sort
                    vec)
        layouts (->> entries
                     keys
                     (filter #(re-matches #"ppt/slideLayouts/slideLayout\d+\.xml" %))
                     sort
                     vec)
        masters (->> entries
                     keys
                     (filter #(re-matches #"ppt/slideMasters/slideMaster\d+\.xml" %))
                     sort
                     vec)]
    {:office-style/kind :style-ir
     :office-style/colors (srgb-colors theme-xml)
     :office-style/fonts (typefaces theme-xml)
     :office-style/slide-size (slide-size presentation-xml)
     :office-style/slides slides
     :office-style/layouts layouts
     :office-style/masters masters}))

(defn extract-bytes [bytes]
  (-> bytes opc/open-pptx style-ir))
