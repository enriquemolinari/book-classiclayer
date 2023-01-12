package layer.data;

class ToBoolean {
  private Boolean booleanValue;

  public ToBoolean(Object value) {
    this.booleanValue = (Boolean) value;
  }

  public boolean val() {
    return this.booleanValue;
  }
}
