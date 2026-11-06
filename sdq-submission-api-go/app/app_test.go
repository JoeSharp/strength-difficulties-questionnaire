package main

import (
	"net/http"
	"net/http/httptest"
	"testing"
	"testing/fstest"

	"github.com/go-chi/chi/v5"
)

func TestFileServerServesSpaRoutesAndStaticAssets(t *testing.T) {
	router := chi.NewRouter()
	fileServer(
		router,
		"",
		http.FS(fstest.MapFS{
			"index.html":        {Data: []byte("app shell")},
			"assets/app.js":     {Data: []byte("app script")},
			"assets/styles.css": {Data: []byte("app styles")},
		}),
	)

	for _, test := range []struct {
		path       string
		wantStatus int
		wantBody   string
	}{
		{path: "/", wantStatus: http.StatusOK, wantBody: "app shell"},
		{path: "/page4", wantStatus: http.StatusOK, wantBody: "app shell"},
		{path: "/assets/app.js", wantStatus: http.StatusOK, wantBody: "app script"},
		{path: "/assets/missing.js", wantStatus: http.StatusNotFound},
		{path: "/api/missing", wantStatus: http.StatusNotFound},
	} {
		t.Run(test.path, func(t *testing.T) {
			recorder := httptest.NewRecorder()
			router.ServeHTTP(recorder, httptest.NewRequest(http.MethodGet, test.path, nil))
			if recorder.Code != test.wantStatus {
				t.Fatalf("status = %d, want %d", recorder.Code, test.wantStatus)
			}
			if test.wantBody != "" && recorder.Body.String() != test.wantBody {
				t.Fatalf("body = %q, want %q", recorder.Body.String(), test.wantBody)
			}
		})
	}
}
