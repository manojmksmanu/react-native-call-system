import React, {useRef} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  Button,
  PermissionsAndroid,
  Platform,
  NativeModules,
  Alert,
} from 'react-native';

import {Colors, Header} from 'react-native/Libraries/NewAppScreen';

const {CallBridge} = NativeModules;

/* -------------------------------
   PERMISSIONS
-------------------------------- */

async function requestCallPermissions(type: 'audio' | 'video') {
  if (Platform.OS !== 'android') return true;

  if (Platform.Version >= 33) {
    const notif = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
    );
    if (notif !== PermissionsAndroid.RESULTS.GRANTED) {
      Alert.alert('Permission required', 'Notification permission needed');
      return false;
    }
  }

  const mic = await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
  );
  if (mic !== PermissionsAndroid.RESULTS.GRANTED) return false;

  if (type === 'video') {
    const cam = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CAMERA,
    );
    if (cam !== PermissionsAndroid.RESULTS.GRANTED) return false;
  }

  return true;
}

/* -------------------------------
   APP
-------------------------------- */

function App(): React.JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  const serviceStartedRef = useRef(false);

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    flex: 1,
  };

  const ensureCoreService = async () => {
    if (serviceStartedRef.current) return;

    const ok = await requestCallPermissions('audio');
    if (!ok) return;

    CallBridge.startCoreService();
    serviceStartedRef.current = true;
  };

  const startAudioCall = async () => {
    const ok = await requestCallPermissions('audio');
    if (!ok) return;

    await ensureCoreService();

    CallBridge.startCallService('Test User', 'audio');
    CallBridge.openCallScreen(); // foreground case
  };

  const startVideoCall = async () => {
    const ok = await requestCallPermissions('video');
    if (!ok) return;

    await ensureCoreService();

    CallBridge.startCallService('Test User', 'video');
    CallBridge.openCallScreen();
  };

  const endCall = () => {
    CallBridge.endCall();
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />

      <ScrollView contentInsetAdjustmentBehavior="automatic">
        <Header />

        <View style={styles.container}>
          <Text style={styles.title}>Native Calling Test</Text>

          <View style={styles.btn}>
            <Button title="Start Audio Call" onPress={startAudioCall} />
          </View>

          <View style={styles.btn}>
            <Button title="Start Video Call" onPress={startVideoCall} />
          </View>

          <View style={styles.btn}>
            <Button title="End Call" color="red" onPress={endCall} />
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {padding: 24},
  title: {fontSize: 22, fontWeight: '700', marginBottom: 20},
  btn: {marginVertical: 10},
});

export default App;
