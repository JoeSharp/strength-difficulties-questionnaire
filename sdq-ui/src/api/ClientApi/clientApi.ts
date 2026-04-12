import type { ClientFile } from "../types";

export const BASE_CLIENT_URL = "/api/client";

export function parseFile(file: ClientFile): ClientFile {
  return {
    ...file,
    dateOfBirth: new Date(file.dateOfBirth),
  };
}
