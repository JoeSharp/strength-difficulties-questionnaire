package uk.ratracejoe.sdq.repository;

import java.util.List;
import uk.ratracejoe.sdq.model.sdq.Category;
import uk.ratracejoe.sdq.model.sdq.Statement;

public interface StatementRepository {
  Statement getStatement(String key);

  List<Statement> getStatements();

  List<Category> getCategories();
}
