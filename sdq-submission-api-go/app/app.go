package main

import (
	"fmt"
	"net/http"
	"os"

	"github.com/go-chi/chi/v5"
	rest "joesharpcs.co.uk/sdq/rest_api"
)

func main() {
	submission_dir := os.Getenv("SUBMISSION_UI_RESOURCES_DIR")
	fmt.Println("Hello Joe SDQ App")
	fmt.Println("Hosting Static resources from", submission_dir)
	r := rest.CreateRestApi()
	fileServer(r, "", http.Dir(submission_dir))
	http.ListenAndServe(":3000", r)
}

func fileServer(r chi.Router, path string, root http.FileSystem) {
	fs := http.StripPrefix(path, http.FileServer(root))

	r.Get(path+"/*", func(w http.ResponseWriter, r *http.Request) {
		fs.ServeHTTP(w, r)
	})
}
