# Come on

## 游戏模块：Bird

## 手势模块：Gesture

## 脑波模块：Mind

- 新建一个SignalDetect对象
- initDevice(Context c)设备初始化，主要用于检测蓝牙是否连接
- connect()设备开启
- getAttention()获取attention值，返回的是int

example：

```
        SignalDetect sd = new SignalDetect();
        int status = sd.initDevice(MainActivity.this);
        sd.connect();
        switch (status) {
            case 1:
                attention = sd.getAttention();
                break;
            case 2:
                Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                break;
            case -1:
                Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                break;
            case 0:
                attention = 0;
                break;
            default:
                break;
        }
```