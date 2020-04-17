# PageStatusTransformer

> 一种Android低入侵性的页面状态切换管理

![Preview](https://github.com/YvesCheung/PageStatusTransformer/blob/master/art/PageStatusTransformer.gif?raw=true)

## 特性

- 构造自定义状态

```kotlin
val transformer =
    PageStatusTransformer.newInstance {

        "普通状态" { 
            SimpleStatus(contentView)
        }

        "加载中" {
            MyLoadingPageStatus()
        }

        "没有数据" {
            MyEmptyPageStatus()
        }

        //可以是任意字符串 "StatusName" { 对应的View }
        //或者是任意枚举 Enum { 对应的View }
        "数据错误" {
            MyErrorPageStatus()
        }
    }
    
//当需要切换状态时，直接切换对应的状态名字：
transformer.transform("没有数据")
//或者枚举
transformer.transform(YourCustomEnum.Status1)
```
可以通过字符串或枚举自定义任意状态，无须受 `showLoading` `showError` 这种死板的接口约束，根据业务场景需要而构造当前页面的状态。


- 指定替换原来的 `View`
```kotlin
PageStatusTransformer.newInstance(
    replaceTo = contentView /*contentView会被替换成同等大小的其他状态的ui*/
) { 
    Status1 {...}
    Status2 {...}
}
```
通过可选参数 `replaceTo` 指定原有页面的 `View` ，当发生状态切换时，新状态的 `View` 会动态替换旧状态上的 `View`。所以无须在XML中放一个所谓的 `StatusLayout`，也无须把业务的状态都塞到 `BaseActivity`/`BaseFragment` 中。

- 支持不同方式的ui切换
```kotlin
PageStatusTransformer.newInstance(...) { 
    Status1 {
        //`SimpleStatus` 只会改变View的可见性
        //当需要显示这个状态时就Visible，当不需要的时候就Gone
        SimpleStatus(view) 
    }
    Status2 {
        //`ViewStubStatus` 会在状态需要显示时才`inflate`对应的`ViewStub`
        //当不需要的时候就Gone
        ViewStubStatus(viewStub)
    }
    Status3 {
        //`ReplacementViewStatus` 会在状态需要显示时通过 `addView` 添加到布局当中，
        //在状态不需要的时候 `removeView` 移除
        ReplacementViewStatus(view)
    }
}
```
支持只改变状态ui的可见性，适用于长期存在于布局中的状态，比如数据正常时的状态。
支持当使用时才渲染的ViewStub，适用于懒加载布局中的状态。
支持动态添加和移除状态ui，适用于短暂存在于布局中的临时状态。

- 自定义状态的ui
```kotlin
PageStatusTransformer.newInstance(replaceTo = ...) { 
    "状态1" {
        object: PageDisplayStatus {
            override fun showView(){
                //每次切换到`Status1`时都会触发
                //完全自定义如何去显示这个状态
            }
            
            override fun hideView(){
                //每次切换到除`Status1`以外的状态都会触发
                //完全自定义如何去隐藏这个状态
            }
        }
    }
    "状态2" {
        object: ReplacementViewStatus() {
            override fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View {
                //切换到`Status2`时触发渲染一个新布局，并动态`addView`到`replaceTo`参数指定的位置
                return inflater.inflate(...., parent, false)
            }
        
            //每次切换到`Status2`时都会触发
            override fun onViewShow(){...}

            //每次切换到除`Status2`以外的状态都会触发
            override fun onViewHide(){...}
        }
    }
    ...
}
```

## 与其他方案对比
目前项目中使用的API都是类似于 `showLoading(@resId int id, String text)` ,但新一期需求设计出的loading提示/空数据提示/网络异常提示都是复杂的动画。以前的接口完全不能满足，而且对`BaseFragment`改动很大。一个本来很小的需求，就影响到接口设计，所以干脆重做了状态切换这一块。

## 对Java支持
通过`JavaViewStatusBuilder`来把弱鸡Java适配到Kotlin DSL：
```Java
PageStatusTransformer transformer = PageStatusTransformer.newInstance(mRecyclerView, 
    new JavaViewStatusBuilder()
        .putStatus("Normal", SimpleStatus(mRecyclerView))
        .putStatus("Empty", YouCustomStatus("暂无数据"))
        .putStatus("Error", YouCustomStatus("网络错误"))
        .putStatus("Loading"，YouCustomStatus("加载中"))
        .build()
);


transformer.transform("Empty");
```
