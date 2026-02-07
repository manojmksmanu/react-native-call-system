import React, {useRef} from 'react';
import {
  SafeAreaView,
  View,
  Button,
  Text,
  PermissionsAndroid,
  Platform,
  NativeModules,
} from 'react-native';

const {CallBridge} = NativeModules;

async function requestPermissions() {
  if (Platform.OS !== 'android') return true;

  if (Platform.Version >= 33) {
    await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
    );
  }

  await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.RECORD_AUDIO);

  return true;
}

export default function App() {
  const coreStarted = useRef(false);

  const ensureCore = async () => {
    if (coreStarted.current) return;
    await requestPermissions();
    CallBridge.startCoreService();
    coreStarted.current = true;
  };

  const startCall = async () => {
    await ensureCore();
    CallBridge.startCallService('Test User', 'audio');
    CallBridge.openCallScreen();
  };

  return (
    <SafeAreaView>
      <View style={{padding: 40}}>
        <Text style={{fontSize: 20}}>Native Call Test</Text>

        <Button title="Start Call" onPress={startCall} />
        <Button
          title="End Call"
          color="red"
          onPress={() => CallBridge.endCall()}
        />
      </View>
    </SafeAreaView>
  );
}
