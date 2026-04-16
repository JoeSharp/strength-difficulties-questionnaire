import { EMPTY_CLIENT_FILE, type ClientFile } from "@/api/ClientApi/clientApi";
import type { GboSubmission, Goal } from "@/api/GboApi/gboApi";
import type { SdqReportingPeriod } from "@/api//SdqApi/sdqApi";

export interface ParsedFile {
  sdqClient: ClientFile;
  goals: Goal[];
  sdq: SdqReportingPeriod[];
  gbo: GboSubmission[];
}

export const EMPTY_PARSED_FILE: ParsedFile = {
  sdqClient: EMPTY_CLIENT_FILE,
  goals: [],
  sdq: [],
  gbo: [],
};
