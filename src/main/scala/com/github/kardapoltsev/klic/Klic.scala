package com.github.kardapoltsev.klic

import android.text._
import android.text.method.PasswordTransformationMethod
import android.view.{View, Gravity}
import android.widget._
import org.scaloid.common._



class Klic extends SActivity with Logger {
  import KeyboardLayouts.KeyboardLayout

  private[this] var input: EditText = _
  private[this] var output: EditText = _
  private[this] var from: Spinner = _
  private[this] var to: Spinner = _

  onCreate {
    setContentView(R.layout.main)
    initLayoutSpinners()
    initInput()
    initOutput()
    initCopyButton()
  }


  private[this] def initLayoutSpinners(): Unit = {
    from = find[Spinner](R.id.from_layout)
    to = find[Spinner](R.id.to_layout)

    val keyboardAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.keyboard_layouts,
      android.R.layout.simple_spinner_item
    )
    keyboardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

    from.setAdapter(keyboardAdapter)
    to.setAdapter(keyboardAdapter)

    to.onItemSelected {
      refreshOutput(input.getText.toString)
    }

  }


  private[this] def initInput(): Unit = {
    input = find[EditText](R.id.input)

    //input filter won't work with custom keyboards completions :(

    //    val inputFilter = new InputFilter {
    //      override def filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence =  {
    //        val selectedLayout = KeyboardLayouts(from.getSelectedItemId.toInt)
    //        val result = source.toString.slice(start, end - start).filter(LayoutConverter.isValid(selectedLayout, _))
    //        source match {
    //          case s: Spanned =>
    //            val sp = new SpannableString(result)
    //            TextUtils.copySpansFrom(s, start, sp.length(), null, sp, 0)
    //            sp
    //          case x =>
    //            result
    //        }
    //      }
    //    }
    //    input.filters = Array(
    //      inputFilter
    //    )

    input.afterTextChanged { s: Editable =>
      if(s.toString.forall(LayoutConverter.isValid(selectedFromLayout, _))) {
        refreshOutput(s.toString)
      } else {
        toast(
          String.format(getString(R.string.invalid_input_for_layout), selectedFromLayout.toString)
        )
      }
    }
  }


  private[this] def refreshOutput(inputText: String): Unit = {
    output.setText(LayoutConverter.convert(
      selectedFromLayout,
      selectedToLayout,
      inputText
    ))
  }


  private[this] def initOutput(): Unit = {
    output = find[EditText](R.id.output)
    output.enabled = false

    def hideOutput(): Unit = {
      output.setTransformationMethod(new PasswordTransformationMethod())
    }
    def showOutput(): Unit = {
      output.setTransformationMethod(null)
    }
    hideOutput()

    val showOutputCheckBox = find[CheckBox](R.id.show_output)
    showOutputCheckBox.checked = false

    showOutputCheckBox.onCheckedChanged { (_: CompoundButton, checked: Boolean) =>
      if(checked) {
        showOutput()
      } else {
        hideOutput()
      }
    }
  }


  private[this] def initCopyButton(): Unit = {
    val button = find[Button](R.id.copy_to_clipboard)
    button.onClick {
      val converted = output.getText.toString
      SystemServices.clipboardManager.text = converted
      toast(R.string.text_copied, gravity = Gravity.CENTER)
      //TODO: use new clipboard manager
      //      val clip = ClipData.newPlainText("label", "Text to copy");
      //      clipboard.setPrimaryClip(clip);
    }
  }


  private[this] def selectedFromLayout: KeyboardLayout = {
    KeyboardLayouts(from.getSelectedItemPosition)
  }


  private[this] def selectedToLayout: KeyboardLayout = {
    KeyboardLayouts(to.getSelectedItemPosition)
  }

}


object KeyboardLayouts extends Enumeration {
  //should be in the same order as in strings.keyboard_layouts
  type KeyboardLayout = Value
  val Russian, Qwerty, Dvorak = Value
}


object LayoutConverter {
  import KeyboardLayouts._

  private[this] val layouts = Map(
    Russian ->
    """
      |ё1234567890-=
      |йцукенгшщзхъ
      |фывапролджэ
      |ячсмитьбю
      |""".toVector,
    Qwerty ->
    """
      |~1234567890-=
      |qwertyuiop[]
      |asdfghjkl;'
      |zxcvbnm,.
      |""".toVector
  )

  assert(layouts(Russian).size == layouts(Qwerty).size)

  def isValid(layout: KeyboardLayout, char: Char): Boolean = {
    layouts(layout).contains(char)
  }

  def convert(from: KeyboardLayout, to: KeyboardLayout, input: String): String = {
    input map { inChar =>
      val i = layouts(from).indexOf(inChar)
      layouts(to)(i)
    }
  }
}
