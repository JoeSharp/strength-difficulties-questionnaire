package rest

import (
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
)

func CreateRestApi() *chi.Mux {
	r := chi.NewRouter()
	r.Use(middleware.Logger)

	// Mount API routes under /api
	r.Route("/api", func(api chi.Router) {
		api.Get("/health", func(w http.ResponseWriter, r *http.Request) {
			w.Write([]byte("ok"))
		})
	})

	return r
}
