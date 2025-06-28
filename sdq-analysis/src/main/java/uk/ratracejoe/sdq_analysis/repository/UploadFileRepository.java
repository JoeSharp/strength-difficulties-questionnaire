package uk.ratracejoe.sdq_analysis.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ratracejoe.sdq_analysis.dto.UploadFile;
import uk.ratracejoe.sdq_analysis.exception.SdqException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.ratracejoe.sdq_analysis.repository.AbstractRepository.handle;

@Service
@RequiredArgsConstructor
public class UploadFileRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileRepository.class);
    private final DataSource dataSource;

    public void saveFile(UploadFile file) throws SdqException {
        handle(dataSource, "saveFile", UploadFileTable.insertSQL(), stmt -> {
            stmt.setString(1, file.uuid().toString());
            stmt.setString(2, file.filename());
            int rowsUpdated = stmt.executeUpdate();
            LOGGER.info("Inserted File to database, rows updated {}", rowsUpdated);
            return rowsUpdated;
        });
    }

    public Optional<UploadFile> getByUUID(UUID uuid) throws SdqException {
        return handle(dataSource, "getByUUID", UploadFileTable.selectByUUID(), stmt -> {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) return Optional.empty();
            return Optional.of(getFromResultSet(rs));
        });
    }

    public List<UploadFile> getAll() throws SdqException {
        return handle(dataSource, "getAll", UploadFileTable.selectAllSQL(), stmt -> {
            List<UploadFile> files = new ArrayList<>();
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(getFromResultSet(rs));
            }
            return files;
        });
    }

    private UploadFile getFromResultSet(ResultSet rs) throws SQLException {
        String uuid = rs.getString("uuid");
        String filename = rs.getString("filename");
        return new UploadFile(UUID.fromString(uuid), filename);
    }

}
