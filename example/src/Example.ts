import {
  Panel,
  Group,
  vlayout,
  layoutConfig,
  Gravity,
  text,
  Color,
  navbar,
  modal,
  Text,
} from "doric";
import { barcodeScanner } from "doric-barcodescanner";

@Entry
class Example extends Panel {
  onShow() {
    navbar(context).setTitle("Example");
  }
  build(rootView: Group) {
    vlayout([
      text({
        text: "Number of cameras",
        textSize: 20,
        backgroundColor: Color.parse("#70a1ff"),
        textColor: Color.WHITE,
        onClick: async function () {
          const number = await barcodeScanner(context).numberOfCameras();
          const tv: Text = this as Text;
          this.text = `Camera number :${number}`;
        },
        layoutConfig: layoutConfig().fit(),
        padding: { left: 20, right: 20, top: 20, bottom: 20 },
      }),
      text({
        text: "Camera 0",
        textSize: 20,
        backgroundColor: Color.parse("#70a1ff"),
        textColor: Color.WHITE,
        onClick: async () => {
          try {
            const result = await barcodeScanner(this.context).scan({
              useCamera: 0,
            });
            await modal(this.context).alert(JSON.stringify(result));
          } catch (e) {
            await modal(this.context).alert(e as string);
          }
        },
        layoutConfig: layoutConfig().fit(),
        padding: { left: 20, right: 20, top: 20, bottom: 20 },
      }),
      text({
        text: "Camera 1",
        textSize: 20,
        backgroundColor: Color.parse("#70a1ff"),
        textColor: Color.WHITE,
        onClick: async () => {
          try {
            const result = await barcodeScanner(this.context).scan({
              useCamera: 1,
            });
            await modal(this.context).alert(JSON.stringify(result));
          } catch (e) {
            await modal(this.context).alert(e as string);
          }
        },
        layoutConfig: layoutConfig().fit(),
        padding: { left: 20, right: 20, top: 20, bottom: 20 },
      }),
    ])
      .apply({
        layoutConfig: layoutConfig().fit().configAlignment(Gravity.Center),
        space: 20,
        gravity: Gravity.Center,
      })
      .in(rootView);
  }
}
