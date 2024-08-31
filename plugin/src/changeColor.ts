import { withAndroidStyles } from '@expo/config-plugins';
import type { ResourceXML } from '@expo/config-plugins/build/android/Resources';
import type { ExpoConfig } from '@expo/config-types';

interface Options {
  primaryColor: string;
}

const withCustomStyles = (config: ExpoConfig, options?: Options) => {
  return withAndroidStyles(config, async (config) => {
    config.modResults = applyCustomStyles(config.modResults, options);
    return config;
  });
};

function applyCustomStyles(styles: ResourceXML, options?: Options) {
  if (options) {
    // Add items to the App Theme
    const customDatePickerTheme = styles.resources.style?.find(
      (style) => style.$.name === 'customDatePickerTheme'
    );

    if (customDatePickerTheme) {
      for (const el of customDatePickerTheme.item) {
        if (el.$.name == 'colorPrimary') {
          el._ = options.primaryColor;
        }
      }
    }

    const customTextButtonStyle = styles.resources.style?.find(
      (style) => style.$.name === 'customTextButtonStyle'
    );

    if (customTextButtonStyle) {
      for (const el of customTextButtonStyle.item) {
        if (el.$.name == 'android:textColor') {
          el._ = options.primaryColor;
        }
      }
    }
  }

  return styles;
}

module.exports = withCustomStyles;
