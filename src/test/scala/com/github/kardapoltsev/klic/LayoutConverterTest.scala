package com.github.kardapoltsev.klic


import org.scalatest.{WordSpec, Matchers}



class LayoutConverterTest extends WordSpec with Matchers {
  import KeyboardLayouts._

  "LayoutConverter" should {
    "convert Russian to Qwerty" in {
      LayoutConverter.convert(Russian, Qwerty, "йцукен") shouldBe "qwerty"
      LayoutConverter.convert(Russian, Qwerty, "бю") shouldBe ",."
      LayoutConverter.convert(Russian, Qwerty, "Ё") shouldBe "~"
    }
    "convert Qwerty to Russian" in {
      LayoutConverter.convert(Qwerty, Russian, "qwerty") shouldBe "йцукен"
    }

    "convert Qwerty to Dvorak" in {
      LayoutConverter.convert(Dvorak, Qwerty, "aoeu") shouldBe "asdf"
      LayoutConverter.convert(Dvorak, Qwerty, "`") shouldBe "`"
      LayoutConverter.convert(Dvorak, Qwerty, "~") shouldBe "~"
      LayoutConverter.convert(Dvorak, Qwerty, "',.pyf") shouldBe "qwerty"
      LayoutConverter.convert(Dvorak, Qwerty, "hello") shouldBe "jdpps"
    }

    "convert Dvorak to Qwerty" in {
      LayoutConverter.convert(Qwerty, Dvorak, "`") shouldBe "`"
      LayoutConverter.convert(Qwerty, Dvorak, "qwerty") shouldBe "',.pyf"
    }
  }

}
