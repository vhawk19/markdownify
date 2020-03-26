(ns markdownify.main
    (:require [reagent.core :as reagent]
              ["showdown" :as showdown]))

(defonce text-state (reagent/atom { :format :md
                                    :value ""}) )

(defonce showdown-converter (showdown/Converter.))


(defn md->html [md]
    (.makeHtml showdown-converter md))

(defn html->md [html]
   (.makeMarkdown showdown-converter html))

(defn ->md [{:keys [format value]}]
    (case format
        :md value
        :html (html->md value)))

(defn ->html[{:keys [format value]}]
    (case format
        :md (md->html value)
        :html value))

(defn copy-to-clipboard [s]
    (let [el (.createElement js/document "textarea")
          selected (when (pos? (-> js/document .getSelection .-rangeCount))
                         (-> js/document .getSelection (.getrangeAt 0)))]
    (set! (.-value el) s)
    (.setAttribute el "readonly" "")
    (set! (-> el .-style .-position) "absolute")
    (set! (-> el .-style .-left) "-9999px")
    (-> js/document .-body (.appendChild el))
    (.select el)
    (.execCommand js/document "copy")
    (-> js/document .-body (.removeChild el))
    (when selected
        (-> js/document .getSelection .removeAllRanges)
        (-> js/document .getSelection (.addRange selected)))))


(defn app []
    [:div
        [:h1 "Markdownify"]
        [:div
            {:style {:display :flex}}
            [:div
                {:style {:flex "1"}}
                [:h2 "Markdown"]
                [:textarea
                {:on-change (fn [e]
                                (reset! text-state {:format :md
                                                    :value (-> e .-target .-value)}))
                :value  (->md @text-state)
                :style {:resize "none"
                        :height "500px"
                        :width "100%"}}]
                [:button  
                    {:on-click #(copy-to-clipboard (->md @text-state))
                     :style {:background-color :green
                             :padding "1em"
                             :color :white
                             :border-radius 10}}
                    "Copy Markdown"]]
            [:div
                {:style {:flex "1"}}
                [:h2 "HTML"]
                [:textarea
                {:on-change (fn [e]
                                (reset! text-state { :format :md
                                                     :value (html->md (-> e .-target .-value))}))
                :value  (->html @text-state)
                :style {:resize "none"
                        :height "500px"
                        :width "100%"}}]
                [:button  
                    {:on-click #(copy-to-clipboard (->html @text-state))
                     :style {:background-color :green
                             :padding "1em"
                             :color :white
                             :border-radius 10}}
                    "Copy HTML"]]
            

            [:div
                {:style {:flex "1"
                         :padding-left "2em"}}
                [:h2 "HTML Preview"]
                [:div {:dangerouslySetInnerHTML {:__html (md->html (:value @text-state)) }
                       :style {:resize "none"
                               :height "500px"
                               :width "100%"}}]
                [:button  
                    {:on-click #(copy-to-clipboard @html)
                     :style {:background-color :green
                             :padding "1em"
                             :color :white
                             :border-radius 10}}
                    "Copy HTML"]]]])

(defn mount! []
    (reagent/render [app]
                    (.getElementById js/document "app")))

(defn main! []
    (println "Welcome to the app")
    (mount!))

(defn reload []
    (println "Reloading")) 