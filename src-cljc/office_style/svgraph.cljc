(ns office-style.svgraph)

(defn presentation [ir]
  {:svgraph/version "svgraph-presentation/1"
   :svgraph/source (:office-style/source ir)
   :svgraph/slide-size (let [{:office-style/keys [cx cy]} (:office-style/slide-size ir)]
                         [(or cx 9144000) (or cy 5143500)])
   :svgraph/theme {:svgraph/colors (:office-style/colors ir)
                   :svgraph/fonts (:office-style/fonts ir)}
   :svgraph/masters (mapv (fn [part] {:svgraph/id part :svgraph/part part})
                          (:office-style/masters ir))
   :svgraph/layouts (mapv (fn [part] {:svgraph/id part :svgraph/part part})
                          (:office-style/layouts ir))
   :svgraph/slides (mapv (fn [part] {:svgraph/id part :svgraph/part part :svgraph/shapes []})
                         (:office-style/slides ir))})
