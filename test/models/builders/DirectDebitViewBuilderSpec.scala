package models.builders

import org.specs2.mutable.Specification
import org.apache.commons.io.IOUtils
import play.api.libs.json.Json

class DirectDebitViewBuilderSpec extends Specification {

  "DirectDebitViewBuilder" should {
    " extract a view from complete json" in {
      val jsonString = IOUtils.toString(
        getClass.getClassLoader.getResourceAsStream("models/builders/payments.json"), "UTF-8")
      val json = Json.parse(jsonString)

      val builder = new DirectDebitViewBuilder
      val view = builder.directDebitView(json)

      view.get.period must beEqualTo("Monthly")
      view.get.accountNumber must beEqualTo("953922355")
      view.get.depository must beEqualTo("Halifax, 1 High Street, Weybridge, KT13 8AA")
    }
  }
}
