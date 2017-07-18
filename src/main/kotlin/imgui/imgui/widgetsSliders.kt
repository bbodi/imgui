package imgui.imgui

import glm_.vec2.Vec2
import imgui.IO
import imgui.ImGui.calcItemWidth
import imgui.ImGui.focusWindow
import imgui.ImGui.focusableItemRegister
import imgui.ImGui.getCurrentWindow
import imgui.ImGui.inputScalarAsWidgetReplacement
import imgui.ImGui.isHovered
import imgui.ImGui.itemAdd
import imgui.ImGui.itemSize
import imgui.ImGui.parseFormatPrecision
import imgui.ImGui.renderText
import imgui.ImGui.renderTextClipped
import imgui.ImGui.setActiveId
import imgui.ImGui.setHoveredId
import imgui.ImGui.sliderBehavior
import imgui.Style
import imgui.internal.DataType
import imgui.internal.Rect
import imgui.Context as g

/** Widgets: Sliders (tip: ctrl+click on a slider to input with keyboard. manually input values aren't clamped, can go
 *  off-bounds)  */
interface imgui_widgetsSliders {


    /** Use power!=1.0 for logarithmic sliders.
     *  Adjust displayFormat to decorate the value with a prefix or a suffix.
     *  "%.3f"         1.234
     *  "%5.2f secs"   01.23 secs
     *  "Gold: %.0f"   Gold: 1  */
    fun sliderFloat(label: String, v: FloatArray, vMin: Float, vMax: Float, displayFormat: String = "%.3f", power: Float = 1f): Boolean {

        val window = getCurrentWindow()
        if (window.skipItems) return false


        val id = window.getId(label)
        val w = calcItemWidth()

        val labelSize = calcTextSize(label, 0, true)
        val frameBb = Rect(window.dc.cursorPos, window.dc.cursorPos + Vec2(w, labelSize.y + Style.framePadding.y * 2f))
        val totalBb = Rect(frameBb.min, frameBb.max + Vec2(if (labelSize.x > 0f) Style.itemInnerSpacing.x + labelSize.x else 0f, 0f))

        // NB- we don't call ItemSize() yet because we may turn into a text edit box below
        if (!itemAdd(totalBb, id)) {
            itemSize(totalBb, Style.framePadding.y)
            return false
        }

        val hovered = isHovered(frameBb, id)
        if (hovered)
            setHoveredId(id)

        val decimalPrecision = parseFormatPrecision(displayFormat, 3)

        // Tabbing or CTRL-clicking on Slider turns it into an input box
        var startTextInput = false
        val tabFocusRequested = focusableItemRegister(window, g.activeId == id)
        if (tabFocusRequested || (hovered && IO.mouseClicked[0])) {
            setActiveId(id, window)
            focusWindow(window)

            if (tabFocusRequested || IO.keyCtrl) {
                startTextInput = true
                g.scalarAsInputTextId = 0
            }
        }
        if (startTextInput || (g.activeId == id && g.scalarAsInputTextId == id))
            return inputScalarAsWidgetReplacement(frameBb, label, DataType.Float, v, id, decimalPrecision)

        itemSize(totalBb, Style.framePadding.y)

        // Actual slider behavior + render grab
        val valueChanged = sliderBehavior(frameBb, id, v, vMin, vMax, power, decimalPrecision)

        // Display value using user-provided display format so user can add prefix/suffix/decorations to the value.
        val value = String.format(displayFormat, v[0])
        renderTextClipped(frameBb.min, frameBb.max, value, value.length, null, Vec2(0.5f, 0.5f))

        if (labelSize.x > 0.0f)
            renderText(Vec2(frameBb.max.x + Style.itemInnerSpacing.x, frameBb.min.y + Style.framePadding.y), label)

        return valueChanged
    }
//    IMGUI_API bool          SliderFloat2(const char* label, float v[2], float v_min, float v_max, const char* display_format = "%.3f", float power = 1.0f);
//    IMGUI_API bool          SliderFloat3(const char* label, float v[3], float v_min, float v_max, const char* display_format = "%.3f", float power = 1.0f);
//    IMGUI_API bool          SliderFloat4(const char* label, float v[4], float v_min, float v_max, const char* display_format = "%.3f", float power = 1.0f);
//    IMGUI_API bool          SliderAngle(const char* label, float* v_rad, float v_degrees_min = -360.0f, float v_degrees_max = +360.0f);
//    IMGUI_API bool          SliderInt(const char* label, int* v, int v_min, int v_max, const char* display_format = "%.0f");
//    IMGUI_API bool          SliderInt2(const char* label, int v[2], int v_min, int v_max, const char* display_format = "%.0f");
//    IMGUI_API bool          SliderInt3(const char* label, int v[3], int v_min, int v_max, const char* display_format = "%.0f");
//    IMGUI_API bool          SliderInt4(const char* label, int v[4], int v_min, int v_max, const char* display_format = "%.0f");
//    IMGUI_API bool          VSliderFloat(const char* label, const ImVec2& size, float* v, float v_min, float v_max, const char* display_format = "%.3f", float power = 1.0f);
//    IMGUI_API bool          VSliderInt(const char* label, const ImVec2& size, int* v, int v_min, int v_max, const char* display_format = "%.0f");
}