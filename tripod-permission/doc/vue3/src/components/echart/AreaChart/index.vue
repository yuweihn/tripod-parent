<template>
	<div>
		<div class="echarts" :id="id" v-loading="chartData.loading" :style="{'height': chartHeight}"></div>
		<el-row :gutter="20">
			<el-col :span="16">&nbsp;</el-col>
			<el-col :span="8" >
				<div class="chart-title">{{maxLabel}}</div>
			</el-col>
		</el-row>
		<el-row :gutter="20" class="details-content">
			<el-col :span="16">
				<div class="line-grid-content">
					<div class="item" v-for="(yaxisItem) in yaxisList">
						<span class="item-head" :style="{'background': yaxisItem.color}"></span><span>{{yaxisItem.alias}}</span>
					</div>
				</div>
			</el-col>
			<el-col :span="8">
				<div class="line-grid-content">
					<div class="item" v-for="(yaxisItem) in yaxisList">
						<span v-if="yaxisItem.max != null">{{yaxisItem.max}} {{yunit}}</span>
					</div>
				</div>
			</el-col>
		</el-row>
	</div>
</template>

<script setup>
import * as echarts from 'echarts';
import { shallowRef } from 'vue';

const props = defineProps({
    id: {
        type: String,
        default: "echart"
    },
    chartHeight: {
        type: String,
        default: "350px"
    },
    chartData: Object
});

const {proxy} = getCurrentInstance();

const chartInstance = shallowRef(null);
const title = ref(null);
const yunit = ref(null);
const maxLabel = ref(null);
const yaxisList = ref([]);

watch(props, (val, oldValue) => {
    drawChart();
});

function initChart() {
    chartInstance.value && chartInstance.value.dispose();
    chartInstance.value = echarts.init(document.getElementById(props.id));
}
function drawChart() {
    chartInstance.value.clear();
    if (!props.chartData || !props.chartData.xaxis) {
        maxLabel.value = null;
        yaxisList.value = [];
        return;
    }
    yunit.value = props.chartData.yunit;
    title.value = props.chartData.title + "(" + yunit.value + ")";
    maxLabel.value = props.chartData.maxLabel;

    var _xDatalist = props.chartData.xaxis.dataList;
    yaxisList.value = props.chartData.yaxisList;

    var _yDataList = [];
    for (let i = 0; i <= yaxisList.value.length - 1; i++) {
        var _yAxis = yaxisList.value[i];
        _yDataList.push({
            name: _yAxis.alias,
            type: 'line',
            smooth: true,
            symbol: 'none',
            areaStyle: {},
            lineStyle: {
                width: 0.3,
                color: _yAxis.color
            },
            itemStyle:{
                color: _yAxis.color
            },
            data: _yAxis.dataList
        });
    }

    var option = {
        tooltip: {
            trigger: 'axis',
            formatter: function(params) {
                var str = "<div style='padding: 0 10px;'>";
                str += "<div>" + params[0].axisValue + "</div>";
                for (var i = 0; i <= params.length - 1; i++) {
                    str += "<div><span style='width:45%;text-align:left;display:inline-block;margin-right:10px;'><span style='background:" + params[i].color + ";width:10px;height:10px;display:inline-block;border-radius:10px;'></span> " + params[i].seriesName + "</span><span style='width:55%;text-align:right;display:inline-block;'><span style='font-weight:bold;'>" + params[i].value + "</span> " + yunit.value + "</span></div>";
                }
                str += "</div>";
                return str;
            }
        },
        title: {
            left: 'center',
            text: title.value,
        },
        toolbox: {
            feature: {
                /**
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {
                    show: false
                },
                **/
                saveAsImage: {}
            }
        },
        dataZoom: [
            {
                type: 'inside',
                start: 0,
                end: 100
            },
            {
                start: 0,
                end: 20
            }
        ],
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: _xDatalist
        },
        yAxis: {
            type: 'value',
            axisLabel: {
                formatter: function(val, idx) {
                    return val;
                }
            }
        },
        series: _yDataList
    };
    chartInstance.value.setOption(option);
    chartInstance.value.resize();
}

onMounted(() => {
    initChart();
    drawChart();
})
</script>

<style scoped>
.echarts {
	width: 100%;
}

.line-grid-content {
	border-radius: 5px;
	min-height: 20px;
	color: #ffffff;
	display: flex;
	justify-content: center;
	align-items: flex-start;
	flex-direction: column;
	padding: 5px 0 5px 15px;
	box-sizing: border-box;
	background: #172b4d;
}

.details-content {
	padding-bottom: 10px;
}
.item-head {
	height: 10px;
	width: 40px;
	display: inline-block;
	margin-right: 4px;
}
.item {
	height: 20px;
	display: flex;
	justify-content: flex-start;
	align-items: center;
}
.chart-title {
	color: #172b4d;
	padding-left: 15px;
	font-size: 16px;
}
</style>
