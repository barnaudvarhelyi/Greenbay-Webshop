package mine.homeworkproject.dtos;

import mine.homeworkproject.models.Transaction;

public class TransactionDto {
  private String name;
  private Boolean transaction;
  private Double amount;
  public TransactionDto(Transaction transaction) {
    this.name = transaction.getAction();
    this.transaction = transaction.getTransaction();
    this.amount = transaction.getAmount();
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Boolean getTransaction() {
    return transaction;
  }
  public void setTransaction(Boolean transaction) {
    this.transaction = transaction;
  }
  public Double getAmount() {
    return amount;
  }
  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
