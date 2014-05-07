(ns leiningen.pallet-minisite.config
  "Configuration functions for pallet mini-site generation"
  (:require
   [clojure.java.io :refer [file]]
   [com.palletops.leinout.core :refer [fail]]
   [com.palletops.leinout.git :as git]
   [com.palletops.leinout.github :as github]
   [leiningen.core.project :as project])
  (:import
   java.io.File
   java.nio.file.Files
   java.nio.file.attribute.FileAttribute))

(defn ^File delete-on-exit
  [^File file]
  (doto file
    .deleteOnExit))

(defn minisite-config
  "Return the mini-site configuration for the project.
  The configuration lives in the project.clj file on the develop
  branch of the repository."
  [project]
  (if-let [origin (git/origin)]
    (let [repo (github/url->repo origin)
          project-str (github/get-file-str repo "project.clj")
          profiles-str (try (github/get-file-str repo "profiles.clj")
                            (catch Exception _
                              {}))
          d (-> (Files/createTempDirectory
                 "minisite" (into-array FileAttribute []))
                .toFile
                delete-on-exit)
          project-clj (-> (file d "project.clj") delete-on-exit)
          profiles-clj (-> (file d "profiles.clj") delete-on-exit)]
      (try
        (spit project-clj project-str)
        (spit profiles-clj profiles-str)
        (-> (project/read (.getPath project-clj) [:default :pallet-project])
            :pallet-project)
        (finally
          (doseq [^File f [project-clj profiles-clj d]]
            (.delete f)))))
    (fail "No origin for repository yet.")))
