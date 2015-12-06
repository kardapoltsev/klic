package com.github.kardapoltsev.klic

import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.widget._
import org.scaloid.common._



class Klic extends SActivity {

  onCreate {
    setContentView(R.layout.main)

    val from = find[Spinner](R.id.from_layout)
    val to = find[Spinner](R.id.to_layout)

    val keyboardAdapter = ArrayAdapter.createFromResource(
      this,
      R.array.keyboard_layouts,
      android.R.layout.simple_spinner_item
    )
    keyboardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    from.setAdapter(keyboardAdapter)
    to.setAdapter(keyboardAdapter)


    val output = find[EditText](R.id.output)
    find[EditText](R.id.input).afterTextChanged { newText: Editable =>
      output.setText(LayoutConverter.convert(newText.toString))
    }

    val showOutputCheckBox = find[CheckBox](R.id.show_output)

    def hideOutput(): Unit = {
      output.setTransformationMethod(new PasswordTransformationMethod())
    }
    def showOutput(): Unit = {
      output.setTransformationMethod(null)
    }
    showOutputCheckBox.checked = false
    hideOutput()

    showOutputCheckBox.onCheckedChanged { (_: CompoundButton, checked: Boolean) =>
      if(checked) {
        showOutput()
      } else {
        hideOutput()
      }
    }

    find[Button](R.id.copy_to_clipboard).onClick {
      val converted = output.getText.toString
      SystemServices.clipboardManager.text = converted
      toast(R.string.text_copied, gravity = Gravity.CENTER)
      //TODO: use new clipboard manager
//      val clip = ClipData.newPlainText("label", "Text to copy");
//      clipboard.setPrimaryClip(clip);
    }
  }

}


object LayoutConverter {
  val Russian = "йцукенгшщзхъфывапролджэячсмитьбю".toCharArray
  val Querty = "qwertyuiop[]asdfghjkl;'zxcvbnm,.".toCharArray

  assert(Russian.size == Querty.size)

  def convert(input: String): String = {
    input map { inChar =>
      val i = Russian.indexOf(inChar)
      Querty(i)
    }
  }
}
