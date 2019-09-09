(ns app.ui.root
  (:require
    [goog.object :as gobj]
    [cljs.spec.alpha :as s]
    [com.fulcrologic.semantic-ui.modules.dropdown.ui-dropdown :as dropdown]
    [app.model.session :as session]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as prim :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [clojure.string :as str]))

;; ;;;;;;;;;;;;;;;;
;; ;; Utils
;; ;;;;;;;;;;;;;;;;

;; (defn field [{:keys [label valid? error-message] :as props}]
;;   (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
;;     (div :.ui.field
;;       (dom/label {:htmlFor label} label)
;;       (dom/input input-props)
;;       (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
;;         error-message))))

;; ;;;;;;;;;;;;;;;;
;; ;; SignUp
;; ;;;;;;;;;;;;;;;;

;; (defsc SignupSuccess [this props]
;;   {:query         ['*]
;;    :initial-state {}
;;    :ident         (fn [] [:component/id :signup-success])
;;    :route-segment ["signup-success"]
;;    :will-enter    (fn [app _] (dr/route-immediate [:component/id :signup-success]))}
;;   (div
;;     (dom/h3 "Signup Complete!")
;;     (dom/p "You can now log in!")))

;; (defsc Signup [this {:account/keys [email password password-again] :as props}]
;;   {:query             [:account/email :account/password :account/password-again fs/form-config-join]
;;    :initial-state     (fn [_]
;;                         (fs/add-form-config Signup
;;                           {:account/email          ""
;;                            :account/password       ""
;;                            :account/password-again ""}))
;;    :form-fields       #{:account/email :account/password :account/password-again}
;;    :ident             (fn [] session/signup-ident)
;;    :route-segment     ["signup"]
;;    :componentDidMount (fn [this]
;;                         (comp/transact! this [(session/clear-signup-form)]))
;;    :will-enter        (fn [app _] (dr/route-immediate [:component/id :signup]))}
;;   (let [submit!  (fn [evt]
;;                    (when (or (identical? true evt) (evt/enter-key? evt))
;;                      (comp/transact! this [(session/signup! {:email email :password password})])
;;                      (log/info "Sign up")))
;;         checked? (log/spy :info (fs/checked? props))]
;;     (div
;;       (dom/h3 "Signup")
;;       (div :.ui.form {:classes [(when checked? "error")]}
;;         (field {:label         "Email"
;;                 :value         (or email "")
;;                 :valid?        (session/valid-email? email)
;;                 :error-message "Must be an email address"
;;                 :autoComplete  "off"
;;                 :onKeyDown     submit!
;;                 :onChange      #(m/set-string! this :account/email :event %)})
;;         (field {:label         "Password"
;;                 :type          "password"
;;                 :value         (or password "")
;;                 :valid?        (session/valid-password? password)
;;                 :error-message "Password must be at least 8 characters."
;;                 :onKeyDown     submit!
;;                 :autoComplete  "off"
;;                 :onChange      #(m/set-string! this :account/password :event %)})
;;         (field {:label         "Repeat Password" :type "password" :value (or password-again "")
;;                 :autoComplete  "off"
;;                 :valid?        (= password password-again)
;;                 :error-message "Passwords do not match."
;;                 :onChange      #(m/set-string! this :account/password-again :event %)})
;;         (dom/button :.ui.primary.button {:onClick #(submit! true)}
;;           "Sign Up")))))

;; (declare Session)

;; ;;;;;;;;;;;;;;;;
;; ;; LogIn
;; ;;;;;;;;;;;;;;;;

;; (defsc Login [this {:account/keys [email]
;;                     :ui/keys      [error open?] :as props}]
;;   {:query         [:ui/open? :ui/error :account/email
;;                    {[:component/id :session] (comp/get-query Session)}
;;                    [::uism/asm-id ::session/session]]
;;    :css           [[:.floating-menu {:position "absolute !important"
;;                                      :z-index  1000
;;                                      :width    "300px"
;;                                      :right    "0px"
;;                                      :top      "50px"}]]
;;    :initial-state {:account/email "" :ui/error ""}
;;    :ident         (fn [] [:component/id :login])}
;;   (let [current-state (uism/get-active-state this ::session/session)
;;         {current-user :account/name} (get props [:component/id :session])
;;         initial?      (= :initial current-state)
;;         loading?      (= :state/checking-session current-state)
;;         logged-in?    (= :state/logged-in current-state)
;;         {:keys [floating-menu]} (css/get-classnames Login)
;;         password      (or (comp/get-state this :password) "")] ; c.l. state for security
;;     (dom/div
;;       (when-not initial?
;;         (dom/div :.right.menu
;;           (if logged-in?
;;             (dom/button :.item
;;               {:onClick #(uism/trigger! this ::session/session :event/logout)}
;;               (dom/span current-user) ent/nbsp "Log out")
;;             (dom/div :.item {:style   {:position "relative"}
;;                              :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
;;               "Login"
;;               (when open?
;;                 (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
;;                                                                         ;; Stop bubbling (would trigger the menu toggle)
;;                                                                         (evt/stop-propagation! e))
;;                                                              :classes [floating-menu]}
;;                   (dom/h3 :.ui.header "Login")
;;                   (div :.ui.form {:classes [(when (seq error) "error")]}
;;                     (field {:label    "Email"
;;                             :value    email
;;                             :onChange #(m/set-string! this :account/email :event %)})
;;                     (field {:label    "Password"
;;                             :type     "password"
;;                             :value    password
;;                             :onChange #(comp/set-state! this {:password (evt/target-value %)})})
;;                     (div :.ui.error.message error)
;;                     (div :.ui.field
;;                       (dom/button :.ui.button
;;                         {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
;;                                                                                              :password password}))
;;                          :classes [(when loading? "loading")]} "Login"))
;;                     (div :.ui.message
;;                       (dom/p "Don't have an account?")
;;                       (dom/a {:onClick (fn []
;;                                          (uism/trigger! this ::session/session :event/toggle-modal {})
;;                                          (dr/change-route this ["signup"]))}
;;                         "Please sign up!"))))))))))))

;; (def ui-login (comp/factory Login))

;; ;;;;;;;;;;;;;;;;
;; ;; Main
;; ;;;;;;;;;;;;;;;;


;; (defsc Main [this props]
;;   {:query         [:main/welcome-message]
;;    :initial-state {:main/welcome-message "Hi!"}
;;    :ident         (fn [] [:component/id :main])
;;    :route-segment ["main"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :main]))}
;;   (div :.ui.container.segment
;;     (h3 "Main")))




;; ;;;;;;;;;;;;;;;;
;; ;; Settings
;; ;;;;;;;;;;;;;;;;

;; (defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
;;   {:query         [:account/time-zone :account/real-name]
;;    :ident         (fn [] [:component/id :settings])
;;    :route-segment ["settings"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :settings]))
;;    :initial-state {}}
;;   (div :.ui.container.segment
;;     (h3 "Settings")))

;; (dr/defrouter TopRouter [this props]
;;   {:router-targets [Main Signup SignupSuccess Settings]})

;; (def ui-top-router (comp/factory TopRouter))

;; ;;;;;;;;;;;;;;;;
;; ;; Session
;; ;;;;;;;;;;;;;;;;

;; (defsc Session
;;   "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
;;   [this {:keys [:session/valid? :account/name] :as props}]
;;   {:query         [:session/valid? :account/name]
;;    :ident         (fn [] [:component/id :session])
;;    :pre-merge     (fn [{:keys [data-tree]}]
;;                     (merge {:session/valid? false :account/name ""}
;;                       data-tree))
;;    :initial-state {:session/valid? false :account/name ""}})

;; (def ui-session (prim/factory Session))

;; ;;;;;;;;;;;;;;;;
;; ;; TopChrome
;; ;;;;;;;;;;;;;;;;

;; (defsc TopChrome [this {:root/keys [router current-session login]}]
;;   {:query         [{:root/router (comp/get-query TopRouter)}
;;                    {:root/current-session (comp/get-query Session)}
;;                    [::uism/asm-id ::TopRouter]
;;                    {:root/login (comp/get-query Login)}]
;;    :ident         (fn [] [:component/id :top-chrome])
;;    :initial-state {:root/router          {}
;;                    :root/login           {}
;;                    :root/current-session {}}}
;;   (let [current-tab (some-> (dr/current-route this this) first keyword)]
;;     (div :.ui.container
;;       (div :.ui.secondary.pointing.menu
;;         (dom/a :.item {:classes [(when (= :main current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["main"]))} "Main")
;;         (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
;;         (div :.right.menu
;;           (ui-login login)))
;;       (div :.ui.grid
;;         (div :.ui.row
;;           (ui-top-router router))))))

;; (def ui-top-chrome (comp/factory TopChrome))

;; ;;;;;;;;;;;;;;;;
;; ;; Bump Number
;; ;;;;;;;;;;;;;;;;

;; (defmutation bump-number [ignored]
;;   (action [{:keys [state]}]
;;           (swap! state update :root/number inc)))

;; (defsc BumpNumber [this {:root/keys [number]}]
;;        {:query         [:root/number]
;;         :initial-state {:root/number 0}}
;;        (dom/div
;;          (dom/h4 "This is an example.")
;;          (dom/button {:onClick #(comp/transact! this `[(bump-number {})])}
;;                      "You've clicked this button " number " times.")))

;; (def ui-bump-number (comp/factory BumpNumber))

;; ;;;;;;;;;;;;;;;;
;; ;; ROOT
;; ;;;;;;;;;;;;;;;;


;; (defsc Root [this {:root/keys [top-chrome]}]
;;   {:query             [{:root/top-chrome (comp/get-query TopChrome)}]
;;    :ident             (fn [] [:component/id :ROOT])
;;    :initial-state     {:root/top-chrome {}}}
;;   (div
;;     (ui-top-chrome top-chrome)
;;     (ui-bump-number {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; VANILLA EXAMPLES
;;;;;;;;;;;;;;;;


(defn update-frame-content [this child]
  (let [frame-component (gobj/get this "frame-component")]
    (when frame-component
      (js/ReactDOM.render child frame-component))))

(defn start-frame [this]
  (let [frame-body (.-body (.-contentDocument (js/ReactDOM.findDOMNode this)))
        {:keys [child]} (comp/props this)
        e1 (.createElement js/document "div")]
    (when (= 0 (gobj/getValueByKeys frame-body #js ["children" "length"]))
      (.appendChild frame-body e1)
      (gobj/set this "frame-component" e1)
      (update-frame-content this child))))

(defsc IFrame [this props]
  {:componentDidMount  (fn [this] (start-frame this))
   :componentDidUpdate (fn [this _ _]
                         (let [child (:child (comp/props this))]
                           (update-frame-content this child)))}

  (dom/iframe
    (-> (comp/props this)
        (dissoc :child)
        (assoc :onLoad #(start-frame this))
        clj->js)))

(let [factory (comp/factory IFrame)]
  (defn ui-iframe [props child]
    (factory (assoc props :child child))))

;;;; actual demo code

(declare Root PhoneForm)

(defn field-attrs
  "A helper function for getting aspects of a particular field."
  [component field]
  (let [form (comp/props component)
        entity-ident (comp/get-ident component form)
        id (str (first entity-ident) "-" (second entity-ident))
        is-dirty? (fs/dirty? form field)
        clean? (not is-dirty?)
        validity (fs/get-spec-validity form field)
        is-invalid? (= :invalid validity)
        value (get form field "")]
    {:dirty?   is-dirty?
     :ident    entity-ident
     :id       id
     :clean?   clean?
     :validity validity
     :invalid? is-invalid?
     :value    value}))

(s/def :phone/number #(re-matches #"\(?[0-9]{3}[-.)]? *[0-9]{3}-?[0-9]{4}" %))

(defmutation abort-phone-edit [{:keys [id]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             ; stop editing
                             (dissoc :root/phone)
                             ; revert to the pristine state
                             (fs/pristine->entity* [:phone/id id])))))
  (refresh [env] [:root/phone]))

(defmutation submit-phone [{:keys [id delta]}]
  (action [{:keys [state]}]
          (swap! state (fn [s]
                         (-> s
                             ; stop editing
                             (dissoc :root/phone)
                             ; update the pristine state
                             (fs/entity->pristine* [:phone/id id])))))
  (remote [env] true)
  (refresh [env] [:root/phone [:phone/id id]]))

(defn input-with-label
  [component field label validation-message input]
  (let [{:keys [dirty? invalid?]} (field-attrs component field)]
    (comp/fragment
      (dom/div :.field {:classes [(when invalid? "error") (when dirty? "warning")]}
               (dom/label {:htmlFor (str field)} label)
               input)
      (when invalid?
        (dom/div :.ui.error.message {} validation-message))
      (when dirty?
        (dom/div :.ui.warning.message {} "(dirty)")))))

(defsc PhoneForm [this {:phone/keys [id type number] :as props}]
  {:query       [:phone/id :phone/type :phone/number fs/form-config-join]
   :form-fields #{:phone/number :phone/type}
   :ident       :phone/id}
  (let [dirty? (fs/dirty? props)
        invalid? (= :invalid (fs/get-spec-validity props))]
    (dom/div :.ui.form {:classes [(when invalid? "error") (when dirty? "warning")]}
             (input-with-label this :phone/number "Phone:" "10-digit phone number is required."
                               (dom/input {:value    (or (str number) "")
                                           :onChange #(m/set-string! this :phone/number :event %)}))
             (input-with-label this :phone/type "Type:" ""
                               (dropdown/ui-dropdown {:value     (name type)
                                                      :selection true
                                                      :options   [{:text "Home" :value "home"}
                                                                  {:text "Work" :value "work"}]
                                                      :onChange  (fn [_ v]
                                                                   (when-let [v (some-> (.-value v) keyword)]
                                                                     (m/set-value! this :phone/type v)))}))
             (dom/button :.ui.button {:onClick #(comp/transact! this [(abort-phone-edit {:id id})])} "Cancel")
             (dom/button :.ui.button {:disabled (or (not (fs/checked? props)) (fs/invalid-spec? props))
                                      :onClick  #(comp/transact! this [(submit-phone {:id id :delta (fs/dirty-fields props true)})])} "Commit!"))))

(def ui-phone-form (comp/factory PhoneForm {:keyfn :phone/id}))

(defsc PhoneNumber [this {:phone/keys [id type number]} {:keys [onSelect]}]
  {:query         [:phone/id :phone/number :phone/type]
   :initial-state {:phone/id :param/id :phone/number :param/number :phone/type :param/type}
   :ident         :phone/id}
  (dom/li :.ui.item.button.secondary
          (dom/a {:onClick (fn [] (onSelect id))}
                 (str number " (" (get {:home "Home" :work "Work" nil "Unknown"} type) ")"))))

(def ui-phone-number (comp/factory PhoneNumber {:keyfn :phone/id}))

(defsc PhoneBook [this {:phonebook/keys [id phone-numbers]} {:keys [onSelect]}]
  {:query         [:phonebook/id {:phonebook/phone-numbers (comp/get-query PhoneNumber)}]
   :initial-state {:phonebook/id            :main
                   :phonebook/phone-numbers [{:id 1 :number "541-555-1212" :type :home}
                                             {:id 2 :number "541-555-5533" :type :work}]}
   :ident         :phonebook/id}
  (dom/div
    (dom/h4 "Phone Book (click a number to edit)")
    (dom/ul
      (map (fn [n] (ui-phone-number (comp/computed n {:onSelect onSelect}))) phone-numbers))))

(def ui-phone-book (comp/factory PhoneBook {:keyfn :phonebook/id}))

(defmutation edit-phone-number [{:keys [id]}]
  (action [{:keys [state]}]
          (let [phone-type (get-in @state [:phone/id id :phone/type])]
            (swap! state (fn [s]
                           (-> s
                               ; make sure the form config is with the entity
                               (fs/add-form-config* PhoneForm [:phone/id id])
                               ; since we're editing an existing thing, we should start it out complete (validations apply)
                               (fs/mark-complete* [:phone/id id])
                               ; tell the root UI that we're editing a phone number by linking it in
                               (assoc :root/phone [:phone/id id])))))))

(defsc Root [this {:keys [:root/phone :root/phonebook]}]
  {:query         [{:root/phonebook (comp/get-query PhoneBook)}
                   {:root/phone (comp/get-query PhoneForm)}]
   :initial-state {:root/phonebook {}
                   :root/phone     {}}}
  (ui-iframe {:frameBorder 0 :width 500 :height 400}
             (dom/div
               (dom/link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.4.1/semantic.min.css"})
               (if (contains? phone :phone/number)
                 (ui-phone-form phone)
                 (ui-phone-book (comp/computed phonebook {:onSelect (fn [id] (comp/transact! this [(edit-phone-number {:id id})]))}))))))



