<template>
  <div class="consultation">
    <div class="page-header">
      <h2>农保咨询专栏</h2>
      <p>专业的农业保险咨询服务，解答您的投保疑问</p>
    </div>

    <!-- 智能问答区域 -->
    <div class="chat-section">
      <h3>农保智慧顾问</h3>
      <div class="chat-container">
        <div class="chat-messages" ref="chatMessages">
          <div v-for="(message, index) in messages" 
            :key="index" 
            :class="['message', message.type === 'user' ? 'user-message' : 'bot-message']"
            :data-show="true"
          >
            <div class="message-avatar">
              <el-icon v-if="message.type === 'user'"><User /></el-icon>
              <el-icon v-else><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="message.content"></div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <el-input
            v-model="userInput"
            placeholder="请输入您的问题，如：什么是农业保险？"
            @keyup.enter="sendMessage"
            :disabled="isTyping"
          >
            <template #append>
              <el-button @click="sendMessage" :loading="isTyping">发送</el-button>
            </template>
          </el-input>
        </div>

        <!-- 快捷提问 -->
        <div class="quick-questions">
          <h4>您可以这样问：</h4>
          <div class="question-tags">
            <el-tag
              v-for="question in quickQuestions"
              :key="question"
              @click="askQuickQuestion(question)"
              class="question-tag"
            >
              {{ question }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 常见问题区域 -->
    <div class="faq-section">
      <h3>常见问题</h3>
      <el-collapse v-model="activeNames">
        <el-collapse-item title="农业保险的基本概念" name="1">
          <div class="faq-content">
            <h4>什么是农业保险？</h4>
            <p>农业保险是为农业生产和农民收入提供保障的保险产品，主要包括：</p>
            <ul>
              <li>种植险：覆盖水稻、小麦等农作物</li>
              <li>养殖险：针对畜禽、水产等养殖业</li>
              <li>农机具险：保护农业机械设备</li>
              <li>设施农业险：温室大棚等农业设施</li>
            </ul>
          </div>
        </el-collapse-item>
        
        <el-collapse-item title="投保指南" name="2">
          <div class="faq-content">
            <h4>如何选择合适的保险产品？</h4>
            <p>选择保险产品时需要考虑以下几点：</p>
            <ul>
              <li>种植作物或农业项目的具体情况</li>
              <li>面临的主要风险类型</li>
              <li>保险费用的承受能力</li>
              <li>保险公司的服务能力和理赔效率</li>
            </ul>
            <h4>投保流程是怎样的？</h4>
            <ol>
              <li>选择合适的保险产品</li>
              <li>准备必要的证明材料（如土地证明、身份证等）</li>
              <li>填写投保单</li>
              <li>支付保费</li>
              <li>等待保单生效</li>
            </ol>
          </div>
        </el-collapse-item>

        <el-collapse-item title="理赔服务" name="3">
          <div class="faq-content">
            <h4>理赔流程是怎样的？</h4>
            <ol>
              <li>及时报案：发生保险事故后24小时内通知保险公司</li>
              <li>现场查勘：保险公司派员现场查看损失情况</li>
              <li>提交材料：准备相关证明材料和单据</li>
              <li>损失评估：确定损失程度和赔付金额</li>
              <li>赔款支付：完成赔付流程</li>
            </ol>
            <h4>需要准备哪些理赔材料？</h4>
            <ul>
              <li>保险单正本或复印件</li>
              <li>出险通知书</li>
              <li>损失清单</li>
              <li>相关部门出具的证明文件</li>
              <li>照片或视频证据</li>
              <li>其他必要的证明文件</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item title="保险责任与免责条款" name="4">
          <div class="faq-content">
            <h4>农业保险通常包括哪些保障？</h4>
            <ul>
              <li>自然灾害（台风、暴雨、冰雹等）</li>
              <li>病虫害损失</li>
              <li>意外事故</li>
              <li>火灾、爆炸等</li>
            </ul>
            <h4>哪些情况不在保障范围内？</h4>
            <ul>
              <li>故意行为造成的损失</li>
              <li>管理不善导致的损失</li>
              <li>非保险责任期间的损失</li>
              <li>战争、军事行动等造成的损失</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item title="保费补贴政策" name="5">
          <div class="faq-content">
            <h4>目前有哪些保费补贴政策？</h4>
            <p>农业保险实行以下保费补贴政策：</p>
            <ul>
              <li>中央财政补贴：通常占保费的35%-40%</li>
              <li>省级财政补贴：约占保费的25%-30%</li>
              <li>市县财政补贴：约占保费的15%-20%</li>
              <li>农户自缴：通常只需承担20%-30%的保费</li>
            </ul>
            <h4>如何申请保费补贴？</h4>
            <ol>
              <li>确认您种植的作物是否属于补贴范围</li>
              <li>通过保险公司提交补贴申请</li>
              <li>准备必要的证明材料</li>
              <li>等待审核和补贴发放</li>
            </ol>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- 人工咨询区域 -->
    <div class="consultation-section">
      <h3>人工咨询</h3>
      <div class="consultation-form">
        <el-form :model="consultForm" label-width="80px">
          <el-form-item label="咨询类型">
            <el-select v-model="consultForm.type" placeholder="请选择咨询类型">
              <el-option label="产品咨询" value="product" />
              <el-option label="理赔咨询" value="claim" />
              <el-option label="补贴政策" value="subsidy" />
              <el-option label="投保流程" value="process" />
              <el-option label="其他问题" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题描述">
            <el-input
              v-model="consultForm.content"
              type="textarea"
              rows="4"
              placeholder="请详细描述您的问题..."
            />
          </el-form-item>
          <el-form-item label="联系方式">
            <el-input v-model="consultForm.contact" placeholder="请留下您的联系方式" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitConsultation">提交咨询</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 联系方式 -->
    <div class="contact-section">
      <h3>联系我们</h3>
      <div class="contact-info">
        <div class="contact-item">
          <el-icon><Phone /></el-icon>
          <span>咨询热线：400-888-8888</span>
        </div>
        <div class="contact-item">
          <el-icon><Message /></el-icon>
          <span>工作时间：周一至周五 9:00-18:00</span>
        </div>
        <div class="contact-item">
          <el-icon><Location /></el-icon>
          <span>地址：浙江省宁波市 农业保险大厦</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Phone, Message, Location, User, Service } from '@element-plus/icons-vue'

const activeNames = ref(['1'])
const chatMessages = ref(null)
const userInput = ref('')
const messages = ref([
  {
    type: 'bot',
    content: '您好！我是农保智慧顾问小慧，很高兴为您服务。您可以询问任何关于农业保险的问题，我会专业、详细地为您解答。',
    time: new Date().toLocaleTimeString()
  }
])

// 快捷问题列表
const quickQuestions = [
  '什么是农业保险？',
  '农业保险有哪些种类？',
  '如何投保农业保险？',
  '农业保险有哪些补贴？',
  '如何申请理赔？',
  '保险费用是多少？',
  '保险责任有哪些？',
  '如何选择保险公司？',
  '理赔需要多长时间？',
  '续保需要注意什么？'
]

// 问答知识库
const knowledgeBase = {
  '什么是农业保险': `农业保险是一种为农业生产和农民收入提供保障的保险产品。主要包括：
  <ul>
    <li>种植险：覆盖水稻、小麦等农作物</li>
    <li>养殖险：针对畜禽、水产等养殖业</li>
    <li>农机具险：保护农业机械设备</li>
    <li>设施农业险：温室大棚等农业设施</li>
  </ul>`,
  
  '如何投保农业保险': `投保农业保险的流程如下：
  <ol>
    <li>选择合适的保险产品</li>
    <li>准备必要的证明材料（身份证、土地证明等）</li>
    <li>填写投保单</li>
    <li>支付保费</li>
    <li>等待保单生效</li>
  </ol>
  您可以通过我们的线上平台或到当地保险公司网点办理。`,
  
  '农业保险有哪些补贴': `目前农业保险的保费补贴政策如下：
  <ul>
    <li>中央财政补贴：35%-40%</li>
    <li>省级财政补贴：25%-30%</li>
    <li>市县财政补贴：15%-20%</li>
    <li>农户自缴：仅需20%-30%</li>
  </ul>
  具体补贴比例可能因地区和保险品种不同而有所差异。`,
  
  '如何申请理赔': `发生保险事故后，理赔流程如下：
  <ol>
    <li>及时报案：24小时内通知保险公司</li>
    <li>现场查勘：等待保险公司人员查看损失情况</li>
    <li>提交材料：准备理赔所需证明文件</li>
    <li>损失评估：确定损失程度和赔付金额</li>
    <li>赔款支付：等待赔付到账</li>
  </ol>`,
  
  '保险费用是多少': `农业保险的费用取决于多个因素：
  <ul>
    <li>保险类型（种植险/养殖险/农机具险等）</li>
    <li>保障范围和保额大小</li>
    <li>种植作物或养殖品种</li>
    <li>地理位置和风险系数</li>
  </ul>
  具体费用可以咨询保险公司或使用我们的在线计算工具。另外，政府提供大量补贴，您实际只需支付很小一部分。`,
  
  '保险责任有哪些': `农业保险的保障范围通常包括：
  <ul>
    <li>自然灾害：台风、暴雨、冰雹、洪水、干旱等</li>
    <li>病虫害损失</li>
    <li>意外事故</li>
    <li>火灾、爆炸等</li>
  </ul>
  同时，以下情况通常不在保障范围内：
  <ul>
    <li>故意行为造成的损失</li>
    <li>管理不善导致的损失</li>
    <li>非保险责任期间的损失</li>
  </ul>`,

  '农业保险的种类': `农业保险主要包括以下几类：
  <ul>
    <li>种植业保险：
      <ul>
        <li>水稻保险</li>
        <li>小麦保险</li>
        <li>玉米保险</li>
        <li>经济作物保险</li>
      </ul>
    </li>
    <li>养殖业保险：
      <ul>
        <li>生猪保险</li>
        <li>奶牛保险</li>
        <li>家禽保险</li>
        <li>水产养殖保险</li>
      </ul>
    </li>
    <li>农业设施保险：
      <ul>
        <li>大棚保险</li>
        <li>农机具保险</li>
        <li>灌溉设施保险</li>
      </ul>
    </li>
  </ul>`,

  '农业保险的优势': `农业保险具有以下优势：
  <ul>
    <li>政府补贴力度大，农户负担小</li>
    <li>保障范围广，覆盖主要农业风险</li>
    <li>理赔程序简单，服务到村到户</li>
    <li>专业风险评估，科学赔付标准</li>
    <li>与农业生产紧密结合，保障更有针对性</li>
  </ul>`,

  '如何选择保险公司': `选择农业保险公司时，建议考虑以下几点：
  <ul>
    <li>公司资质：选择具有农业保险经营资质的正规保险公司</li>
    <li>服务网络：优先考虑在当地有服务网点的保险公司</li>
    <li>理赔效率：关注公司的理赔速度和服务质量</li>
    <li>产品种类：选择产品种类丰富、能满足多样化需求的公司</li>
    <li>口碑评价：参考其他农户的投保体验和评价</li>
  </ul>`,

  '农业保险的投保时间': `不同农业保险的投保时间有所不同：
  <ul>
    <li>种植业保险：通常在播种前或生长初期投保</li>
    <li>养殖业保险：可在养殖周期开始时投保</li>
    <li>设施农业保险：建议在设施建成后及时投保</li>
  </ul>
  特别提醒：
  <ul>
    <li>部分保险有等待期规定</li>
    <li>自然灾害来临前的突击投保可能无效</li>
    <li>建议提前规划，合理安排投保时间</li>
  </ul>`,

  '保险金额如何确定': `农业保险金额的确定通常考虑以下因素：
  <ul>
    <li>投保标的价值：
      <ul>
        <li>种植业：按照种植面积和每亩产值</li>
        <li>养殖业：按照存栏数量和单位价值</li>
        <li>农机具：按照设备实际价值</li>
      </ul>
    </li>
    <li>风险程度：不同地区自然灾害风险不同</li>
    <li>历史赔付情况：参考往年理赔数据</li>
    <li>农户承受能力：结合实际经济情况</li>
  </ul>`,

  '理赔需要多长时间': `农业保险理赔时间一般如下：
  <ul>
    <li>一般流程：
      <ul>
        <li>报案后24小时内现场查勘</li>
        <li>资料齐全后5-10个工作日完成赔付</li>
        <li>重大案件可能需要15-20个工作日</li>
      </ul>
    </li>
    <li>特殊情况：
      <ul>
        <li>自然灾害集中发生时可能延长</li>
        <li>需要专家鉴定的可能需要更长时间</li>
      </ul>
    </li>
  </ul>`,

  '农业保险的赔付标准': `农业保险赔付标准主要包括：
  <ul>
    <li>种植业：
      <ul>
        <li>全损：按照约定保险金额赔付</li>
        <li>部分损失：按照损失程度比例赔付</li>
        <li>自然灾害：根据灾害等级确定赔付比例</li>
      </ul>
    </li>
    <li>养殖业：
      <ul>
        <li>死亡赔付：按照保险价格赔付</li>
        <li>疾病赔付：按照治疗费用比例赔付</li>
      </ul>
    </li>
    <li>农机具：
      <ul>
        <li>全损：按照实际价值赔付</li>
        <li>部分损失：按照维修费用赔付</li>
      </ul>
    </li>
  </ul>`,

  '保险合同注意事项': `投保农业保险时需要注意以下合同条款：
  <ul>
    <li>保险责任：
      <ul>
        <li>明确保障范围和保险期限</li>
        <li>了解免责条款和除外责任</li>
        <li>确认赔付标准和理赔流程</li>
      </ul>
    </li>
    <li>投保人义务：
      <ul>
        <li>如实告知农业生产情况</li>
        <li>按时缴纳保险费</li>
        <li>遵守安全生产规范</li>
      </ul>
    </li>
    <li>特别约定：
      <ul>
        <li>等待期规定</li>
        <li>理赔限额</li>
        <li>续保条件</li>
      </ul>
    </li>
  </ul>`,

  '农业保险的续保': `关于农业保险续保的说明：
  <ul>
    <li>续保优势：
      <ul>
        <li>可能享受续保优惠</li>
        <li>无需重新等待期</li>
        <li>理赔记录可延续</li>
      </ul>
    </li>
    <li>续保注意事项：
      <ul>
        <li>及时办理续保手续</li>
        <li>更新风险信息</li>
        <li>关注保障内容变化</li>
      </ul>
    </li>
    <li>续保建议：
      <ul>
        <li>评估上一年度保障效果</li>
        <li>根据需求调整保障方案</li>
        <li>对比不同保险公司产品</li>
      </ul>
    </li>
  </ul>`
}

// 智能匹配问题
const findAnswer = (question) => {
  // 将问题转换为关键词，移除标点符号
  const keywords = question.replace(/[？?.,。，]/g, '').split(/\s+/)
  
  // 遍历知识库寻找最匹配的答案
  let bestMatch = null
  let maxMatchCount = 0
  
  // 首先尝试完全匹配
  for (const [key, value] of Object.entries(knowledgeBase)) {
    // 如果问题完全匹配知识库中的某个问题
    if (question.includes(key)) {
      return value
    }
  }
  
  // 如果没有完全匹配，则进行关键词匹配
  for (const [key, value] of Object.entries(knowledgeBase)) {
    let matchCount = 0
    keywords.forEach(keyword => {
      if (key.includes(keyword)) {
        matchCount += 2  // 关键词在问题中出现权重更高
      }
      if (value.toLowerCase().includes(keyword)) {
        matchCount += 1
      }
    })
    
    if (matchCount > maxMatchCount) {
      maxMatchCount = matchCount
      bestMatch = value
    }
  }
  
  // 如果找到了匹配的答案且匹配度足够高
  if (bestMatch && maxMatchCount >= 1) {
    return bestMatch
  }
  
  // 如果没有找到匹配的答案，返回默认回复
  return `抱歉，我暂时无法回答这个问题。您可以：
  <ul>
    <li>换个方式提问</li>
    <li>查看上方的常见问题</li>
    <li>联系人工客服获取帮助</li>
  </ul>`
}

const isTyping = ref(false)

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim()) return
  
  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: userInput.value,
    time: new Date().toLocaleTimeString(),
    animate: true
  })
  
  // 清空输入
  const question = userInput.value
  userInput.value = ''
  
  // 滚动到底部
  await nextTick()
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }

  // 显示正在输入状态
  isTyping.value = true
  messages.value.push({
    type: 'bot',
    content: `<div class="bot-typing">
      <div class="typing-dots">
        正在思考中<span class="dot">.</span><span class="dot">.</span><span class="dot">.</span>
      </div>
    </div>`,
    time: new Date().toLocaleTimeString(),
    isTyping: true
  })

  // 滚动到底部
  await nextTick()
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }

  // 模拟网络延迟
  await new Promise(resolve => setTimeout(resolve, 1500))

  // 移除输入状态消息
  messages.value = messages.value.filter(msg => !msg.isTyping)
  
  // 添加机器人回复
  const answer = findAnswer(question)
  messages.value.push({
    type: 'bot',
    content: answer,
    time: new Date().toLocaleTimeString(),
    animate: true
  })
  
  // 关闭输入状态
  isTyping.value = false
  
  // 滚动到底部
  await nextTick()
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}

// 快捷提问
const askQuickQuestion = (question) => {
  userInput.value = question
  sendMessage()
}

const consultForm = ref({
  type: '',
  content: '',
  contact: ''
})

const submitConsultation = () => {
  if (!consultForm.value.type || !consultForm.value.content || !consultForm.value.contact) {
    ElMessage.warning('请填写完整的咨询信息')
    return
  }
  
  // TODO: 这里添加提交咨询的接口调用
  ElMessage.success('咨询提交成功，我们会尽快联系您')
  consultForm.value = {
    type: '',
    content: '',
    contact: ''
  }
}
</script>

<style scoped>
.consultation {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background: #f8fafc;
}

.page-header {
  margin-bottom: 40px;
  text-align: center;
  position: relative;
  padding: 30px 0;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 15px rgba(82, 196, 26, 0.2);
}

.page-header h2 {
  font-size: 32px;
  margin-bottom: 15px;
  color: white;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.page-header p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.chat-section,
.faq-section,
.consultation-section,
.contact-section {
  background: #fff;
  padding: 25px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.chat-section::before,
.faq-section::before,
.consultation-section::before,
.contact-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #52c41a, #389e0d);
}

.chat-container {
  border: 1px solid #e8f4ff;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.chat-messages {
  height: 450px;
  overflow-y: auto;
  padding: 25px;
  background: #f8fafc;
  scrollbar-width: thin;
  scrollbar-color: #52c41a #f0f0f0;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #52c41a;
  border-radius: 3px;
}

.message {
  display: flex;
  margin-bottom: 25px;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-message {
  flex-direction: row-reverse;
  justify-content: flex-start;
}

.message[data-show="true"] {
  opacity: 1;
  transform: translateY(0);
}

.message-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid #e8f7e8;
}

.user-message .message-avatar {
  margin-right: 0;
  margin-left: 15px;
  background: #52c41a;
  color: #fff;
  border-color: #73d13d;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-text {
  padding: 15px 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  line-height: 1.6;
  font-size: 14px;
  position: relative;
}

.bot-message .message-text {
  background: #f8fafc;
  border: 1px solid #e8f7e8;
  border-radius: 3px 12px 12px 12px;
}

.user-message .message-text {
  background: #52c41a;
  color: #fff;
  border-radius: 12px 3px 12px 12px;
}

.message-time {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 6px;
  text-align: right;
}

.chat-input {
  padding: 20px;
  border-top: 1px solid #e8f4ff;
  background: #fff;
}

.quick-questions {
  padding: 20px;
  border-top: 1px solid #e8f4ff;
  background: #f8fafc;
}

.quick-questions h4 {
  margin-bottom: 15px;
  color: #262626;
  font-weight: 600;
}

.question-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.question-tag {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 13px;
}

.question-tag:hover {
  background: #52c41a;
  color: #fff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.2);
}

h3 {
  margin-bottom: 25px;
  color: #262626;
  font-size: 22px;
  font-weight: 600;
  position: relative;
  padding-left: 15px;
}

h3::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: #52c41a;
  border-radius: 2px;
}

.faq-content {
  color: #595959;
}

.faq-content h4 {
  color: #262626;
  margin: 20px 0 12px;
  font-weight: 600;
}

.faq-content ul,
.faq-content ol {
  padding-left: 20px;
  margin: 12px 0;
  line-height: 1.8;
}

.consultation-form {
  max-width: 700px;
  margin: 0 auto;
  padding: 30px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e8f4ff;
}

.contact-section {
  text-align: center;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}

.contact-info {
  display: flex;
  justify-content: center;
  gap: 60px;
  flex-wrap: wrap;
  margin-top: 30px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px 25px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.contact-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.contact-item .el-icon {
  font-size: 24px;
  color: #52c41a;
}

.contact-item span {
  color: #262626;
  font-size: 15px;
}

/* 机器人输入动画 */
.bot-typing {
  padding: 15px 20px;
  background: #f0f7eb;
  border-radius: 12px;
  display: inline-block;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.1);
}

.typing-dots {
  font-size: 15px;
  color: #52c41a;
  display: flex;
  align-items: center;
}

.typing-dots .dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  margin: 0 2px;
  background: #52c41a;
  border-radius: 50%;
  animation: dotJump 1.2s infinite;
}

@keyframes dotJump {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.6;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

/* 响应式布局优化 */
@media (max-width: 768px) {
  .consultation {
    padding: 10px;
  }
  
  .page-header {
    padding: 20px 15px;
    margin-bottom: 20px;
  }
  
  .page-header h2 {
    font-size: 24px;
  }
  
  .contact-info {
    flex-direction: column;
    gap: 15px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .chat-messages {
    height: 400px;
    padding: 15px;
  }
  
  .consultation-form {
    padding: 20px;
  }
}

/* 添加卡片悬浮效果 */
.chat-section:hover,
.faq-section:hover,
.consultation-section:hover {
  transform: translateY(-2px);
  transition: transform 0.3s ease;
}

/* 优化折叠面板样式 */
:deep(.el-collapse-item__header) {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  background: #f8fafc;
  border-radius: 6px;
  padding: 15px;
}

:deep(.el-collapse-item__content) {
  padding: 20px;
  background: #fff;
  border-radius: 0 0 6px 6px;
}

/* 优化输入框样式 */
:deep(.el-input__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}

/* 优化按钮和输入框样式 */
:deep(.el-button--primary) {
  background: #52c41a;
  border-color: #52c41a;
}

:deep(.el-button--primary:hover) {
  background: #73d13d;
  border-color: #73d13d;
}

:deep(.el-button--primary:active) {
  background: #389e0d;
  border-color: #389e0d;
}

:deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 1px #52c41a inset;
}

:deep(.el-tag) {
  background: #f6ffed;
  border-color: #b7eb8f;
  color: #389e0d;
}

:deep(.el-tag:hover) {
  color: #fff;
  background: #52c41a;
  border-color: #52c41a;
}

:deep(.el-select-dropdown__item.selected) {
  color: #52c41a;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #52c41a;
  border-color: #52c41a;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #52c41a;
  border-color: #52c41a;
}

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #52c41a;
  border-color: #52c41a;
}
</style> 