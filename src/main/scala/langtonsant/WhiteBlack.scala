package langtonsant

sealed trait WhiteBlack {
  def toggle: WhiteBlack
}

case object White extends WhiteBlack {
  override def toggle: WhiteBlack = Black
}

case object Black extends WhiteBlack {
  override def toggle: WhiteBlack = White
}
