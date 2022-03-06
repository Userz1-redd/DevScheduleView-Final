# DevScheduleView

- DevScheduleView는 사용자의 일정을 일간, 주간으로 보여줄 수 있는 커스텀 라이브러리 입니다. 

<img width="212" alt="image" src="https://open.oss.navercorp.com/storage/user/900/files/5e70a800-943d-11ec-8e88-6794ccb41a5a">  <img width="211" alt="image" src="https://open.oss.navercorp.com/storage/user/900/files/7ea06700-943d-11ec-9bff-e8e5d20e2b26"> 


## Features

- Schedules Of Members Day View

- Schedules Of Members Week View

- Horizontal and Vertical Scrolling

- Horizontal Infinite Scrolling

- Paging

- Click Listener

- Schedule Drag and Drop

- Custom Option

## Usage

1. 라이브러리를 프로젝트에 Import 합니다.

- Maven
```
```

- Gradle
```
```

2. layout 내에 DevScheduleView를 추가합니다.

```
 <com.example.library.mainview.DevScheduleView
                        android:id="@+id/devschedule_view"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        app:visibleNumberOfMembers="1"
                        app:viewType="0"/>
```

visibleNumberOfMembers는 한 화면에 나타낼 사용자의 수입니다. viewType은 0(일간뷰), 1(주간뷰)에 맞게 사용할 수 있습니다.

3. 현재 보여줄 일정의 날짜를 반드시 설정해야 합니다. setDate(Time)으로 설정할 수 있습니다. 기본적으로 뷰 바인딩, 또는 데이터 바인딩 사용을 권장합니다.

Time객체는 연, 월, 일, 시, 분, 요일로 구성된 객체입니다.

```
binding.devscheduleView.setDate(Time(2022,1,25,10,30,"FRI")
```

4. devScheduleView.submitUserList, setList로 User및 ScheduleList를 등록합니다.

타입은 ArrayList<User> 입니다. 사용자의 필요에 따라 내장 데이터베이스를 이용하거나, 외부 API를 이용하여 적절히 사용할 수 있습니다.

라이브러리에 사용되는 객체는 User, Schedule, Time 총 3가지 입니다.

<img width="99" alt="image" src="https://user-images.githubusercontent.com/58676668/152457496-6f27c9cc-1fa8-425c-871d-300786431f71.png"> <img width="119" alt="image" src="https://user-images.githubusercontent.com/58676668/152457577-34128560-ee3e-4b97-a9e4-680a0473c1d7.png"> <img width="98" alt="image" src="https://user-images.githubusercontent.com/58676668/152457601-799e543c-f179-4c00-8231-cfcd86e987a4.png">

DevScheduleView에 유저리스트를 등록하는 방법은 페이징을 사용하는 방법, 모든 유저 정보를 한꺼번에 등록하는 방법이 있습니다.

* 모든 유저 정보를 한번에 등록하는 경우

기존 설명과 같이 setDate(Time)으로 먼저 날짜를 설정합니다. 

User(id, name, ArrayList<Schedule>)을 담은 ArrayList<User> 객체를 binding.devscheduleView.submitList(userList)로 전달하면 해당되는 스케줄을 화면에 자동으로 그리게 됩니다.

화면에 돌아왔을 때, 자동으로 새로고침이 필요한 경우에 기존의 유저리스트를 로컬에서 저장하고 있다면 아래와 같이 사용합니다. 

```
override fun onResume(){
    binding.devscheduleView.submitUserList(ArrayList(userList))
    super.onResume()
}
```
userList가 로컬에 저장되어 있지 않은 경우 binding.devscheduleView.getUserList()를 통해 현재 등록되어 있는 UserList를 얻을 수 있습니다.

*	페이징을 사용하는 경우

마찬가지로 setDate(Time)으로 먼저 날짜를 설정합니다.

커스텀뷰에는 페이징을 사용하여 데이터를 추가할 때, 현재 스크롤값에 따라 다음 요청할 페이지를 전달받을 수 있는 리스너가 등록되어 있으며 아래와 같이 사용할 수 있습니다.
전달받은 페이지에 맞춰 binding.devscheduleView.setList(userList)를 추가합니다. 
```
binding.devscheduleView.setOnPageChangeListener(object : OnPageChangeListener{
    override fun onPageChange(page: Int) {
        // 필요한 만큼의 데이터를 로드하고, setList(userList)를 통해 추가
    }
})

```

MVVM을 사용하는 경우 아래와 같이 필요에 따라 데이터를 로드하고, LiveData의 변경에 맞춰 setList를 실행합니다.

```
binding.devscheduleView.setOnPageChangeListener(object : OnPageChangeListener{
    override fun onPageChange(page: Int) {
        viewModel.loadUserListPaging(page)
    }
})
```

```
viewModel.pagingUserList.observe(this,{
    binding.devscheduleView.setList(it)
})
```

마찬가지로 화면에 돌아올때 마다 새로고침하고자 하는경우, 아래와 같이 코드를 추가합니다.

```
override fun onResume(){
    binding.devscheduleView.submitUserList(ArrayList())
    binding.devscheduleView.resetCurrentPage()
    super.onResume()
}

```

## Attribute

- viewType : Integer = 0(일간 뷰)/1(주간 뷰)

- 명시적으로 사용하는 경우 setViewType()을 사용하여 뷰타입을 지정할 수 있습니다.

- 버튼의 onClick동작에서 setViewType으로 뷰타입을 변경하는 것을 권장합니다.

- visibleNumberOfMembers : 한 화면에 표시할 사용자의 수


## Interfaces

필요에 따라 OnClickNameHeaderListener, OnClickScheduleListener, OnClickTimeHeaderListener, OnDateChangeListener, OnPageChangeListener, OnScheduleDropListener를 override하여 사용할 수 있습니다.

기본적으로 NameHeader클릭시 해당 포지션의 높이를 증가하고, 스케줄 클릭시 해당 스케줄에 대한 정보를 제공하며 시간 헤더 클릭은 아무것도 지정되어 있지 않습니다.

기본적으로 제공되는 리스너를 제거하고 커스텀으로 제작하고 싶다면 아래와 같이 사용할 수 있습니다.

```
binding.devscheduleView.setOnClickNameHeaderListener(object : OnClickNameHeaderListener{
            override fun onClickNameHeader(position: Int) {
                //Implement according to your need
            }
})
binding.devscheduleView.setOnClickScheduleListener(object : OnClickScheduleListener{
            override fun onClickSchedule(position: Int, scheduleidx: Int) {
                //Implement according to your need
                //해당 스케줄의 정보는 userList[position].scheduleList[scheduleidx]로 조회 가능합니다.
            }
            override fun onClickEmptySchedule() {
                //Implement according to your need
            }
})
binding.devscheduleView.setOnClickTimeHeaderListener(object : OnClickTimeHeaderListener{
            override fun onClickTimeHeader() {
                //Implement according to your need
            }

})
```

OnDateChangeListener는 주간, 일간뷰에서 스크롤의 좌측 끝, 또는 우측끝으로 이동하여 날짜가 변경되는 경우 호출되며 변경된 날짜를 Time객체로 전달받습니다. 

변경된 날짜에 맞춰 사용자가 원하는 동작을 정의할 수 있습니다.

```
binding.devscheduleView.setOnDateChangeListener(object : OnDateChangeListener{
            override fun OnDateChange(date: Time) {
               ///원하는 동작 정의
            }
        })
```

OnPageChangeListener는 스크롤 위치에 맞춰 다음 요청할 페이지를 전달받아 동작을 정의합니다. 자세한 사항은 4번에 명시되어 있습니다.

```
binding.devscheduleView.setOnPageChangeListener(object : OnPageChangeListener{
    override fun onPageChange(page: Int) {
        // 필요한 만큼의 데이터를 로드하고, setList(userList)를 통해 추가
    }
})

```

OnScheduleDropListener는 드래그앤 드롭 사용시 드롭된 스케줄의 정보를 받아 데이터를 수정할 수 있도록 변경된 사용자의 ID와 일정 정보를 전달합니다.

```
binding.devscheduleView.setOnScheduleDropListener(object : OnScheduleDropListener {
    override fun onScheduleDrop(changeUserId: String, schedule: Schedule) {
        viewModel.modifySchedule(changeUserId, schedule)
    }
})
```


## Sample

- There is also a sample app to get you started

