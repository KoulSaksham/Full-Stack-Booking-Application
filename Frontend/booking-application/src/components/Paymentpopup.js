import React from 'react';
import { Modal, Select, Input, Button, Form } from 'antd';

const { Option } = Select;

function PaymentPopup({ visible, onClose, onPay, allowedPayments }) {
  const [form] = Form.useForm();
  const paymentMethod = Form.useWatch('paymentMethod', form); // ✅ Tracks real-time changes

  const renderPaymentFields = () => {
    switch (paymentMethod) {
      case 'CARD':
        return (
          <>
            <Form.Item name="cardNumber" label="Card Number" rules={[{ required: true }]}>
              <Input placeholder="1234 5678 9012 3456" />
            </Form.Item>
            <Form.Item name="expiryDate" label="Expiry Date" rules={[{ required: true }]}>
              <Input placeholder="MM/YY" />
            </Form.Item>
            <Form.Item name="cvv" label="CVV" rules={[{ required: true }]}>
              <Input placeholder="123" />
            </Form.Item>
          </>
        );
      case 'UPI':
        return (
          <Form.Item name="upiId" label="UPI ID" rules={[{ required: true }]}>
            <Input placeholder="user@upi" />
          </Form.Item>
        );
      case 'CASH':
        return (
          <Form.Item name="cashNote" label="Note" rules={[{ required: true }]}>
            <Input placeholder="Paying by cash on arrival" />
          </Form.Item>
        );
      case 'NETBANKING':
        return (
          <>
            <Form.Item name="bankName" label="Bank" rules={[{ required: true }]}>
              <Input placeholder="Bank name" />
            </Form.Item>
            <Form.Item name="txnId" label="User ID" rules={[{ required: true }]}>
              <Input placeholder="abcd@123" />
            </Form.Item>
          </>
        );
      default:
        return null;
    }
  };

  const handleFinish = (values) => {
    if (!values.paymentMethod) {
      alert('Please select a payment method');
      return;
    }

    // Successfully selected and submitted
    onPay(values.paymentMethod);
    onClose();
  };

  return (
    <Modal title="Enter Payment Details" open={visible} onCancel={onClose} footer={null}>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
      >
        <Form.Item
          label="Payment Method"
          name="paymentMethod"
          rules={[{ required: true, message: 'Please select a payment method' }]}
        >
          <Select placeholder="Select a method" allowClear>
            {allowedPayments.map((method, idx) => (
              <Option key={idx} value={method}>{method}</Option>
            ))}
          </Select>
        </Form.Item>

        {renderPaymentFields()}

        <Form.Item>
          <Button type="primary" block htmlType="submit">
            Confirm Payment
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
}

export default PaymentPopup;
