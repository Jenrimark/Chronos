# 辰序 Chronos

一个功能完善的Android日历应用，遵循RFC5545（iCalendar）规范实现。

## 功能特性

### 基本功能（已实现）

#### 1. 日历视图展示
- **月视图（Month View）**：以网格形式展示整月日历，支持查看每日的日程标记
- **周视图（Week View）**：展示一周内的所有日程列表
- **日视图（Day View）**：展示单日内的详细日程安排
- 支持左右滑动切换不同时间段
- 今日高亮显示，快速返回今天功能

#### 2. 日程管理
- **添加日程**：点击日期或FAB按钮创建新日程
- **编辑日程**：点击已有日程进行修改
- **删除日程**：支持删除确认对话框
- **查看日程**：点击日程查看详细信息

##### 日程属性
- 标题（必填）
- 开始时间和结束时间
- 全天事件选项
- 地点
- 详细描述
- 自定义颜色
- 提醒设置

#### 3. 日程提醒
- 支持多种提醒时间：
  - 准时提醒
  - 提前5分钟
  - 提前15分钟
  - 提前30分钟
  - 提前1小时
  - 提前1天
- 使用WorkManager实现后台定时检查
- 系统通知栏提醒
- 点击通知直接打开应用

## 技术实现

### 架构
- **MVVM架构模式**
- **Repository模式**用于数据访问
- **ViewBinding**用于视图绑定

### 核心技术栈
- **Kotlin** - 主要开发语言
- **Room Database** - 本地数据持久化
- **LiveData & ViewModel** - 响应式数据和生命周期管理
- **Coroutines** - 异步编程
- **WorkManager** - 后台任务调度
- **ViewPager2** - 页面滑动切换
- **RecyclerView** - 列表展示
- **Material Design 3** - UI设计规范

### RFC5545规范支持
本应用参考RFC5545（iCalendar）规范实现，主要包括：
- **SUMMARY**：日程标题
- **DESCRIPTION**：详细描述
- **LOCATION**：地点信息
- **DTSTART**：开始时间
- **DTEND**：结束时间
- **STATUS**：日程状态（TENTATIVE, CONFIRMED, CANCELLED）
- **RRULE**：重复规则（预留扩展接口）

## 项目结构

```
com.chronos.calendar/
├── data/                          # 数据层
│   ├── Event.kt                   # 事件实体类
│   ├── EventDao.kt                # 数据访问对象
│   ├── EventDatabase.kt           # Room数据库
│   ├── EventRepository.kt         # 数据仓库
│   └── Converters.kt              # 类型转换器
│
├── viewmodel/                     # 视图模型层
│   └── EventViewModel.kt          # 事件视图模型
│
├── ui/                            # UI层
│   ├── MainActivity.kt            # 主Activity
│   ├── CalendarViewMode.kt        # 视图模式枚举
│   ├── CalendarViewPagerAdapter.kt# ViewPager适配器
│   ├── MonthViewFragment.kt       # 月视图Fragment
│   ├── WeekViewFragment.kt        # 周视图Fragment
│   ├── DayViewFragment.kt         # 日视图Fragment
│   ├── MonthCalendarAdapter.kt    # 月历适配器
│   ├── EventListAdapter.kt        # 事件列表适配器
│   └── EventBottomSheetFragment.kt# 事件编辑底部弹窗
│
├── notification/                  # 通知层
│   ├── EventReminderWorker.kt     # 提醒Worker
│   └── ReminderScheduler.kt       # 提醒调度器
│
└── ChronosApplication.kt          # Application类
```

## 使用说明

### 基本操作
1. **查看日历**：启动应用后默认显示月视图，可通过菜单切换周视图或日视图
2. **添加日程**：
   - 点击右下角的"+"按钮
   - 或点击日历中的日期
   - 填写日程信息后点击"添加日程"按钮
3. **查看/编辑日程**：点击已有日程卡片
4. **删除日程**：在编辑界面点击"删除日程"按钮
5. **切换视图**：点击右上角菜单，选择"月视图"/"周视图"/"日视图"
6. **返回今天**：点击右上角的"今天"按钮

### 提醒设置
1. 在添加或编辑日程时，选择"提醒"下拉菜单
2. 选择合适的提醒时间
3. 应用会在指定时间发送通知提醒

## 运行要求
- Android SDK 24+（Android 7.0及以上）
- 推荐使用Android Studio Hedgehog或更高版本

## 权限说明
- `POST_NOTIFICATIONS`：用于发送日程提醒通知
- `SCHEDULE_EXACT_ALARM`：用于精确定时提醒

## 编译和运行
1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击运行按钮

## 扩展功能建议
以下功能可在未来版本中实现：
- [ ] 日程重复功能（每天/每周/每月/每年）
- [ ] 多日历支持（工作/个人/节假日等）
- [ ] 日程搜索功能
- [ ] 数据导入/导出（iCalendar格式）
- [ ] 与系统日历同步
- [ ] 小部件支持
- [ ] 日程分享功能
- [ ] 主题自定义
- [ ] 数据云同步

## 开发者
Chronos Calendar - 辰序日历应用

## 许可证
本项目仅供学习和研究使用。

