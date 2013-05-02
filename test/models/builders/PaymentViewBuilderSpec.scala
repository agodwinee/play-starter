package models.builders

import org.specs2.mutable.Specification
import org.apache.commons.io.IOUtils
import play.api.libs.json.Json

class PaymentViewBuilderSpec extends Specification {

  "PaymentViewBuilder" should {
    "generate tax events from the supplied JSON" in {
      val jsonString = IOUtils.toString(
        getClass.getClassLoader.getResourceAsStream("models/builders/payments.json"), "UTF-8")
      val json = Json.parse(jsonString)

      val paymentViewBuilder = new PaymentViewBuilder
      val view = paymentViewBuilder.paymentView(json)

      view.payments.size mustEqual(12)
      view.payments.head.amount mustEqual(BigDecimal("720.07"))
      view.payments.head.due mustEqual(1367884800000L)
      view.payments.head.eventType mustEqual("TimeToPayDirectDebit")

      view.period mustEqual("Monthly")
      view.created mustEqual(1367229741302L)
      view.amountOwed mustEqual(BigDecimal("9345.60"))
    }
  }
}
