import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Secloap App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const openPlatform = MethodChannel('com.scholarlab/open');
  static const downloadPlatform = MethodChannel('com.scholarlab/download');

  Future<void> _startActivity() async {
    try {
      final String result = await openPlatform.invokeMethod('StartSecondActivity');
      debugPrint('Result: $result');
    } on PlatformException catch (e) {git init
      debugPrint("Error: '${e.message}'.");
    }
  }

  Future<void> _callDownloadActivity() async {
    try {
      final String result = await downloadPlatform.invokeMethod('DownloadModule');
      debugPrint('Result: $result');
    } on PlatformException catch (e) {
      debugPrint("Error: '${e.message}'.");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Secloap App'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: _callDownloadActivity,
              child: const Text('Download Module'),
            ),
            ElevatedButton(
              onPressed: _startActivity,
              child: const Text('Start Activity'),
            ),
          ],
        ),
      ),
    );
  }
}
