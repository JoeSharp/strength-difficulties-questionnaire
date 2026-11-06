package main

import (
	"errors"
	"fmt"
	"net/http"
	"os"
	"path"
	"strings"

	"github.com/go-chi/chi/v5"
	db "joesharpcs.co.uk/sdq/db"
	domain "joesharpcs.co.uk/sdq/domain"
	rest "joesharpcs.co.uk/sdq/rest_api"
)

func main() {
	submission_dir := os.Getenv("SUBMISSION_UI_RESOURCES_DIR")
	db_url := os.Getenv("DATABASE_URL")

	app := domain.SdqApp{
		ClientRepository: db.CreateDb(db_url),
	}

	fmt.Println("Hello Joe SDQ App", app)
	fmt.Println("Hosting Static resources from", submission_dir)
	fmt.Println("Connecting to Database at", db_url)
	r := rest.CreateRestApi(app.ClientRepository)
	fileServer(r, "", http.Dir(submission_dir))
	http.ListenAndServe(":3000", r)
}

func fileServer(r chi.Router, prefix string, root http.FileSystem) {
	fs := http.StripPrefix(prefix, http.FileServer(root))

	r.Get(prefix+"/*", func(w http.ResponseWriter, r *http.Request) {
		requestPath := strings.TrimPrefix(r.URL.Path, prefix)
		if requestPath == "" {
			requestPath = "/"
		}

		file, err := root.Open(requestPath)
		if err == nil {
			file.Close()
			fs.ServeHTTP(w, r)
			return
		}
		if !errors.Is(err, os.ErrNotExist) {
			http.Error(w, "Unable to serve requested resource", http.StatusInternalServerError)
			return
		}
		if requestPath == "/api" || strings.HasPrefix(requestPath, "/api/") || path.Ext(requestPath) != "" {
			http.NotFound(w, r)
			return
		}

		r.URL.Path = prefix + "/"
		fs.ServeHTTP(w, r)
	})
}
