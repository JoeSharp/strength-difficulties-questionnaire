import { EMPTY_CLIENT_FILE, type ClientFile } from "@/api/ClientApi/clientApi";
import type { GboScore } from "@/api/GboApi/gboApi";
import type { SdqScore } from "@/api//SdqApi/sdqApi";

export interface ParsedFile {
  clientFile: ClientFile;
  sdq: SdqScore[];
  gbo: GboScore[];
}

export const EMPTY_PARSED_FILE: ParsedFile = {
  clientFile: EMPTY_CLIENT_FILE,
  sdq: [],
  gbo: [],
};
