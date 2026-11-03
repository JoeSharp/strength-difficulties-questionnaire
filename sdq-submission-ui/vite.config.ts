import { defineConfig } from "vite";
import react, { reactCompilerPreset } from "@vitejs/plugin-react";
import babel from "@rolldown/plugin-babel";
import path from "path";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), babel({ presets: [reactCompilerPreset()] })],
  resolve: {
    alias: {
      "@": path.resolve(import.meta.dirname, "src"),
    },
  },
  server: {
    proxy: {
      "/api": {
        target: process.env.SUBMISSION_API_PROXY_TARGET ?? "http://localhost:8082",
        changeOrigin: true,
      },
    },
    watch: {
      usePolling: process.env.VITE_USE_POLLING === "true",
    },
  },
});
